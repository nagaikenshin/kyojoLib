package org.kyojo.core;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.io.IOLayer;

public class Skin {

	private static final Log logger = LogFactory.getLog(Skin.class);

	private String ext = null;

	private String skinDPath = null;

	private boolean isLogSearchTemplete = false;

	public String getExt() {
		return ext;
	}

	public Skin(GlobalData gbd, SessionData ssd, String ext) {
		this.ext = ext;

		IOLayer ioLayer = gbd.get(IOLayer.class);
		this.skinDPath = ioLayer.getSkinDPath(ssd);

		Object val = gbd.get("IS_LOG_SEARCH_TEMPLETE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogSearchTemplete = true;
		}
	}

	public boolean hasSkin(String act, String key) {
		return getSkinFile(act, key, null) != null;
	}

	public File getSkinFile(String act, String key, Cache cache) {
		key = key.replaceAll(File.pathSeparator, "/").replaceAll("/+", "/").replaceAll("\\.+", ".");

		File f = null;
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
					f = new File(skinDPath + act2.toString() + File.separator + key + "." + ext);

					if(f.canRead()) {
						if(cache != null) {
							if(isLogSearchTemplete) {
								logger.debug(f.getCanonicalPath() + " is used.");
							}
							cache.addRefFile(f.getCanonicalPath());
						}
						withAct = true;
						break;
					} else {
						if(cache != null) {
							if(isLogSearchTemplete) {
								logger.debug(f.getCanonicalPath() + " is not found.");
							}
							cache.addMissFile(f.getCanonicalPath());
						}
					}
				}
			}

			if(!withAct) {
				f = new File(skinDPath + key + "." + ext);
				if(f.canRead()) {
					if(cache != null) {
						if(isLogSearchTemplete) {
							logger.debug(f.getCanonicalPath() + " is used.");
						}
						cache.addRefFile(f.getCanonicalPath());
					}
				} else {
					if(cache != null) {
						if(isLogSearchTemplete) {
							logger.debug(f.getCanonicalPath() + " is not found.");
						}
						cache.addMissFile(f.getCanonicalPath());
					}
					f = new File(skinDPath + key + "." + Constants.FINAL_EXT);
					if(f.canRead()) {
						if(cache != null) {
							if(isLogSearchTemplete) {
								logger.debug(f.getCanonicalPath() + " is used.");
							}
							cache.addRefFile(f.getCanonicalPath());
						}
					} else {
						if(cache != null) {
							if(isLogSearchTemplete) {
								logger.debug(f.getCanonicalPath() + " is not found.");
							}
							cache.addMissFile(f.getCanonicalPath());
						}
						return null;
					}
				}
			}
		} catch(Exception ex) {
			logger.error(ex);
		}

		return f;
	}

}
