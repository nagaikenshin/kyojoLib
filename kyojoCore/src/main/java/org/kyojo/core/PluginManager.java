package org.kyojo.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.io.IOLayer;
import org.kyojo.minion.My;

import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceConnector;
import groovy.util.ResourceException;

public class PluginManager implements ResourceConnector {

	private static final Log logger = LogFactory.getLog(PluginManager.class);

	private static PluginManager instance = null;

	private GlobalData gbd = null;

	private GroovyScriptEngine gse = null;

	private String pluginDPath = null;

	private boolean isLogSearchTemplete = false;

	private PluginManager(GlobalData gbd) {
		this.gbd = gbd;
		gse = createGroovyScriptEngine();
		IOLayer ioLayer = gbd.get(IOLayer.class);
		this.pluginDPath = ioLayer.getPluginDPath();

		Object val = this.gbd.get("IS_LOG_SEARCH_TEMPLETE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogSearchTemplete = true;
		}
	}

	private GroovyScriptEngine createGroovyScriptEngine() {
		return new GroovyScriptEngine(this);
	}

	public static PluginManager getInstance(GlobalData gbd) {
		if(instance == null) {
			instance = new PluginManager(gbd);
		}
		return instance;
	}

	public boolean hasPlugin(String act, String key, SessionData ssd) {
		File f = getPluginFile(act, key, null, ssd);
		return f != null;
	}

	protected class PluginFileInfo {
		File file;
		String mPath;
		String pkg;
	}

	public File getPluginFile(String act, String key, Cache cache, SessionData ssd) {
		PluginFileInfo pfInfo = getPluginFile2(act, key, cache, ssd);
		return pfInfo.file;
	}

	protected PluginFileInfo getPluginFile2(String act, String key, Cache cache, SessionData ssd) {
		IOLayer ioLayer = gbd.get(IOLayer.class);
		String[] pluginPkgs = ioLayer.getPluginPkgs(ssd).split(",");
		key = key.replaceAll(File.pathSeparator, "/").replaceAll("/+", "/").replaceAll("\\.+", ".");

		PluginFileInfo pfInfo = new PluginFileInfo();
		pfInfo.mPath = "";
		try {
			boolean withAct = false;
			if(StringUtils.isNotBlank(act) && key.indexOf("/") < 0) {
				act = act.replaceAll(File.pathSeparator, "/").replaceAll("/+", "/").replaceAll("\\.+", ".");
				String[] elms = act.split("/");
				for(int ei = elms.length - 1; ei >= 0; ei--) {
					// actの上位に遡って検索
					StringBuilder act2 = new StringBuilder();
					for(int ei2 = 0; ei2 <= ei; ei2++) {
						if(ei2 > 0) act2.append("/");
						act2.append(elms[ei2]);
					}
					pfInfo.mPath = act2.toString();
					for(String pluginPkg : pluginPkgs) {
						pfInfo.file = new File(pluginDPath + pluginPkg + pfInfo.mPath + File.separator + key + ".groovy");
						pfInfo.pkg = pluginPkg;

						if(pfInfo.file.canRead()) {
							if(cache == null) {
								if(isLogSearchTemplete) {
									logger.debug(pfInfo.file.getCanonicalPath() + " is used.");
								}
							} else {
								cache.addRefFile(pfInfo.file.getCanonicalPath());
							}
							withAct = true;
							break;
						} else {
							if(cache == null) {
								if(isLogSearchTemplete) {
									logger.debug(pfInfo.file.getCanonicalPath() + " is not found.");
								}
							} else {
								cache.addMissFile(pfInfo.file.getCanonicalPath());
							}
							pfInfo.file = null;
							pfInfo.pkg = null;
						}
					}
					if(withAct) {
						break;
					}
				}
			}

			if(!withAct) {
				pfInfo.mPath = "";
				for(String pluginPkg : pluginPkgs) {
					pfInfo.file = new File(pluginDPath + pluginPkg + key + ".groovy");
					pfInfo.pkg = pluginPkg;
					if(pfInfo.file.canRead()) {
						if(cache == null) {
							if(isLogSearchTemplete) {
								logger.debug(pfInfo.file.getCanonicalPath() + " is used.");
							}
						} else {
							cache.addRefFile(pfInfo.file.getCanonicalPath());
						}
						break;
					} else {
						if(cache == null) {
							if(isLogSearchTemplete) {
								logger.debug(pfInfo.file.getCanonicalPath() + " is not found.");
							}
						} else {
							cache.addMissFile(pfInfo.file.getCanonicalPath());
						}
						pfInfo.file = null;
						pfInfo.pkg = null;
					}
				}
			}
		} catch(Exception ex) {
			logger.error(ex);
		}

		return pfInfo;
	}

	private String sKey2ClassName(String sKey) {
		StringBuilder sb = new StringBuilder();
		String[] elems = sKey.split("/");
		for(int idx = 0; idx < elems.length - 1; idx++) {
			sb.append(elems[idx]);
			if(idx > 0) {
				sb.append(".");
			}
		}
		sb.append(My.pascalize(elems[elems.length - 1]));

		return sb.toString();
	}

	public Class<?> loadPlugin(String act, String sKey, SessionData ssd) {
		PluginFileInfo pfInfo = getPluginFile2(act, sKey, null, ssd);
		ClassLoader loader = this.getClass().getClassLoader();
		try {
			if(pfInfo.file != null) {
				if(pfInfo.file.canRead()) {
					String dlm = StringUtils.isEmpty(pfInfo.mPath) ? "" : ".";
					String className = (pfInfo.pkg + pfInfo.mPath).replaceAll("/", ".")
							+ dlm + sKey2ClassName(sKey);
					try {
						return loader.loadClass(className);
					} catch(ClassNotFoundException cnfe) {
						dlm = StringUtils.isEmpty(pfInfo.mPath) ? "" : "/";
						String scriptName = pfInfo.pkg + pfInfo.mPath + dlm + sKey + ".groovy";
						return gse.loadScriptByName(scriptName);
					}
				}
				if(isLogSearchTemplete) {
					logger.debug(pfInfo.file.getCanonicalPath() + " is not found.");
				}
			}
		} catch(Exception ex) {
			logger.error(ex);
		}

		return null;
	}

	@Override
	public URLConnection getResourceConnection(String name) throws ResourceException {
		URLConnection groovyScriptConn = null;

		ResourceException se = null;
		URL scriptURL = null;
		try {
			File f = new File(pluginDPath);
			scriptURL = new URL(f.toURI().toURL(), name);
			groovyScriptConn = openConnection(scriptURL);
		} catch(MalformedURLException mue) {
			String message = "Malformed URL: " + pluginDPath + name;
			se = new ResourceException(message);
		} catch(IOException ioe) {
			String message = "Cannot open URL: " + pluginDPath + name;
			se = new ResourceException(message);
		}

		if (se == null) se = new ResourceException("No resource for " + name + " was found");

		if (groovyScriptConn == null) throw se;
		return groovyScriptConn;
	}

	private static URLConnection openConnection(URL scriptURL) throws IOException {
		URLConnection urlConnection = scriptURL.openConnection();
		verifyInputStream(urlConnection);

		return scriptURL.openConnection();
	}

	private static void verifyInputStream(URLConnection urlConnection) throws IOException {
		InputStream in = null;
		try {
			in = urlConnection.getInputStream();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch(IOException ignore) {
				}
			}
		}
	}

}
