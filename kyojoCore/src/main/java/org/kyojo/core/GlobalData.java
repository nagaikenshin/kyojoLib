package org.kyojo.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.annotation.OutOfGlobalData;
import org.kyojo.core.io.CacheIO;
import org.kyojo.core.io.DefaultIOLayer;
import org.kyojo.core.io.IOLayer;

public final class GlobalData extends ConcurrentHashMap<String, Object> {

	private static final Log logger = LogFactory.getLog(GlobalData.class);

	private static final long serialVersionUID = -6928081982874321939L;

	private static GlobalData instance = null;

	private ServletContext context = null;

	public ServletContext getContext() {
		return context;
	}

	private Pattern tbpt = null;

	public <T> T get(Class<T> cls) {
		return cls.cast(super.get(cls.getName()));
	}

	public <T> T put(Class<T> cls, T obj) {
		return cls.cast(super.put(cls.getName(), obj));
	}

	private boolean isLogSaveAndLoadResult = false;

	private GlobalData(ServletContext context) {
		this.context = context;
		tbpt = Pattern.compile("\\t");
		loadConstants();

		Object val = get("IS_LOG_SAVE_AND_LOAD_RESULT");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogSaveAndLoadResult = true;
		}
	}

	public static GlobalData getInstance(ServletContext context) throws IOException {
		if(instance == null) {
			instance = new GlobalData(context);
		}
		return instance;
	}

	private void loadConstants() {
		try {
			for(Field field : Constants.class.getDeclaredFields()) {
				int mdfrs = field.getModifiers();
				if(Modifier.isPublic(mdfrs) && Modifier.isStatic(mdfrs) && Modifier.isFinal(mdfrs)
						&& field.getAnnotation(OutOfGlobalData.class) == null) {
					put(field.getName(), field.get(null));
				}
			}
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		InputStream is = null;
		Properties props = null;
		try {
			is = GlobalData.class.getResourceAsStream("Constants.properties");
			props = new Properties();
			props.load(is);
			for(Entry<Object, Object> entry : props.entrySet()){
				put(entry.getKey().toString(), entry.getValue());
			}
			is.close();
			is = null;

			Map<String, String> ioPaths = new HashMap<>();
			ioPaths.put("FILE_DPATH", "file");
			ioPaths.put("CACHE_DPATH", "cache");
			ioPaths.put("SKIN_DPATH", "skin");
			ioPaths.put("PLUGIN_DPATH", "plugin");
			ioPaths.put("PATH_DPATH", "path");
			String webInfPath = context.getRealPath("/WEB-INF");
			for(Map.Entry<String, String> ent : ioPaths.entrySet()) {
				if(!containsKey(ent.getKey()) || get(ent.getKey()).toString().length() == 0) {
					put(ent.getKey(), webInfPath + File.separator + ent.getValue() + File.separator);
				}
			}

			IOLayer ioLayer = null;
			if(containsKey("IO_LAYER_CLASS")) {
				String ioLayerClassName = get("IO_LAYER_CLASS").toString();
				try {
					Class<?> ioLayerClass = GlobalData.class.getClassLoader().loadClass(ioLayerClassName);
					if(IOLayer.class.isAssignableFrom(ioLayerClass)) {
						ioLayer = (IOLayer)ioLayerClass.newInstance();
						ioLayer.initialize(this);
					}
				} catch(ClassNotFoundException cnfe) {
					logger.error(cnfe.getMessage(), cnfe);
				} catch(InstantiationException ie) {
					logger.error(ie.getMessage(), ie);
				} catch(IllegalAccessException iae) {
					logger.error(iae.getMessage(), iae);
				} catch(IOException ioe) {
					logger.error(ioe.getMessage(), ioe);
				} catch(Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
			if(ioLayer == null) {
				ioLayer = new DefaultIOLayer();
				ioLayer.initialize(this);
			}
			put(IOLayer.class, ioLayer);
		} catch(IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			try {
				if(is != null) {
					is.close();
				}
			} catch(IOException ioe) {}
		}
	}

	public Cache loadCacheIfPossible(String sKey, String dKey, String eKey, boolean isRealLoad) {
		Cache cache = null;
		String key = Cache.concatKeys(sKey, dKey, eKey);
		JavaFGets jfg = null;
		Time14 now = new Time14();
		Time14 created = null;
		Time14 expires = null;
		String tmpStr;
		IOLayer ioLayer = get(IOLayer.class);
		CacheIO cacheIO = null;

		try {
			cacheIO = ioLayer.acquireCacheIO(key);
			InputStream is = cacheIO.getInputStream();
			if(is != null) {
				jfg = new JavaFGets(is);

				// cache file opened normally
				// all cache files have >=6 lines
				tmpStr = jfg.readLine();
				tmpStr = StringUtils.trim(tmpStr);
				created = new Time14(tmpStr);
				tmpStr = jfg.readLine();
				tmpStr = StringUtils.trim(tmpStr);
				expires = new Time14(tmpStr);

				String line;
				cache = new Cache(sKey, dKey, eKey, created);
				cache.setExpires(expires);
				line = jfg.readLine();
				line = StringUtils.trim(line);
				if(line.length() > 0) {
					String[] missFiles = tbpt.split(line);
					for(String fpath: missFiles) {
						cache.addMissFile(fpath);
					}
				}
				line = jfg.readLine();
				line = StringUtils.trim(line);
				if(line.length() > 0) {
					String[] refFiles = tbpt.split(line);
					for(String fpath: refFiles) {
						cache.addRefFile(fpath);
					}
				}
				line = jfg.readLine();
				line = StringUtils.trim(line);
				if(line.length() > 0) {
					String[] childKeys = tbpt.split(line);
					for(String childKey: childKeys) {
						// cache.addChildKey(childKey, Time14.OLD);
						cache.addChildKey(childKey, Time14.FUTURE);
					}
				}
				jfg.readLine();
				if(isRealLoad) {
					while((line = jfg.readLine()) != null) {
						cache.addLine(new String(line.getBytes("ISO-8859-1"), "UTF-8"));
					}
				}
			}
		} catch(FileNotFoundException fnfe) {
			if(isLogSaveAndLoadResult) {
				logger.info("no cache \"" + key + "\"");
			}
		} catch(IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
		} finally {
			if(jfg != null) {
				try {
					jfg.close();
				} catch(IOException ioe) {}
			}
			if(cacheIO != null) {
				try {
					cacheIO.release();
				} catch(IOException ioe) {}
			}
		}

		if(cache == null) {
			if(isLogSaveAndLoadResult) {
				logger.info("no cache \"" + key + "\"");
			}
			return null;
		}

		if(expires.compareTo(now) < 0) {
			ioLayer.deleteCache(key);
			if(isLogSaveAndLoadResult) {
				logger.info("no cache \"" + key + "\"");
			}
			return null;
		}

		// missed files created?
		Iterator<String> it = cache.getMissFiles();
		while(it.hasNext()) {
			if(ioLayer.canReadOfFileSystem(it.next())) {
				expires = created;
				break;
			}
		}

		if(expires.compareTo(now) < 0) {
			ioLayer.deleteCache(key);
			if(isLogSaveAndLoadResult) {
				logger.info("no cache \"" + key + "\"");
			}
			return null;
		}

		// reference files updated?
		it = cache.getRefFiles();
		long lm;
		Time14 lmT14;
		while(it.hasNext()) {
			lm = ioLayer.lastModifiedOfFileSystem(it.next());
			if(lm == 0L) {
				expires = created;
				break;
			} else {
				lmT14 = new Time14(new Date(lm));
				if(cache.getCreated().compareTo(lmT14) < 0) {
					expires = created;
					break;
				}
			}
		}

		if(expires.compareTo(now) < 0) {
			ioLayer.deleteCache(key);
			if(isLogSaveAndLoadResult) {
				logger.info("no cache \"" + key + "\"");
			}
			return null;
		}

		// child caches expired?
		it = cache.getChildKeys();
		String[] keyAry;
		Cache child;
		HashSet<Cache> tmpChildren = new HashSet<Cache>();
		while(it.hasNext()) {
			String tmpKey = it.next();
			keyAry = Cache.splitKey(tmpKey);
			if(sKey.equals(keyAry[0]) && dKey.equals(keyAry[1])) {
				// prevent loop
				expires = created;
				break;
			}
			child = loadCacheIfPossible(keyAry[0], keyAry[1], keyAry[2], false);
			if(child == null || cache.getCreated().compareTo(child.getCreated()) < 0) {
				expires = created;
				break;
			} else {
				tmpChildren.add(child);
			}
		}
		for(Cache tmpChild: tmpChildren) {
			cache.addChildKey(tmpChild.getKey(), tmpChild.getExpires());
		}

		if(expires.compareTo(now) < 0) {
			ioLayer.deleteCache(key);
			if(isLogSaveAndLoadResult) {
				logger.info("no cache \"" + key + "\"");
			}
			return null;
		}

		if(isLogSaveAndLoadResult) {
			logger.info("cache found \"" + key + "\"");
		}
		return cache;
	}

	public void completeCache(Cache cache) {
		String nowT14 = (new Time14()).toString();
		if(cache.getExpires().compareTo(nowT14) <= 0) {
			return;
		}

		IOLayer ioLayer = get(IOLayer.class);
		CacheIO cacheIO = null;
		OutputStream os = null;

		try {
			cacheIO = ioLayer.acquireCacheIO(cache.getKey());
			os = cacheIO.getOutputStream();
			if(os != null) {
				// final String ENCODING = "ISO-8859-1";
				final String ENCODING = "UTF-8";
				os.write(cache.getCreated().toString().getBytes(ENCODING));
				os.write('\n');
				os.write(cache.getExpires().toString().getBytes(ENCODING));
				os.write('\n');

				String sep = "";
				Iterator<String> it = cache.getMissFiles();
				while(it.hasNext()) {
					os.write((sep + it.next()).getBytes(ENCODING));
					sep = "\t";
				}
				os.write('\n');

				sep = "";
				it = cache.getRefFiles();
				while(it.hasNext()) {
					os.write((sep + it.next()).getBytes(ENCODING));
					sep = "\t";
				}
				os.write('\n');

				sep = "";
				it = cache.getChildKeys();
				while(it.hasNext()) {
					os.write((sep + it.next()).getBytes(ENCODING));
					sep = "\t";
				}
				os.write('\n');
				os.write('\n');

				// Iterator<String> itr = cache.getLinesAndSaveMemory();
				Iterator<String> itr = cache.getLines();
				while(itr.hasNext()) {
					String line = itr.next();
					os.write(line.getBytes(ENCODING));
				}
			}
		} catch(IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
		} finally {
			if(os != null) {
				try {
					os.close();
				} catch(IOException ioe) {}
			}
			if(cacheIO != null) {
				try {
					cacheIO.release();
				} catch(IOException ioe) {}
			}
		}
	}

}
