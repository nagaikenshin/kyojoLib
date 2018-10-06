package org.kyojo.core;

import org.kyojo.core.annotation.OutOfGlobalData;

public class Constants {

	@OutOfGlobalData
	public static final String RESERVED_EXTS = "htaccess,comment,trackback,role,aliasfrom,aliasto,index,lock";

	public static final String RQD_PREFIX = "rqd";
	public static final String GBD_PREFIX = "gbd";
	public static final String SSD_PREFIX = "ssd";
	public static final String PATH_KEY = "path";
	public static final String ACT_KEY = "act";
	public static final String EXT_KEY = "ext";

	public static final String TEMPLATE_ROOT = "root";
	public static final String FINAL_EXT = "bare";
	public static final String DEFAULT_EXT = "html";
	public static final String AJAX_EXT = "ajax";
	public static final String ERROR_SKIN = "error";
	public static final String TIMEOUT_SKIN = "timeout";
	public static final String DEFAULT_ACT = "index";
	public static final String DEFAULT_PATH = "/";
	public static final String MINION_SUFFIX = "minion";
	public static final String DEMION_SUFFIX = "demion";
	public static final String LIST_NUMBER_SUFFIX = "num";
	public static final String LIST_LAST_SUFFIX = "last";
	public static final String MAP_NUMBER_SUFFIX = "num";
	public static final String PLG_INITIALIZE_MTD_NAME = "initialize";
	// public static final String PLG_GET_D_KEY_MTD_NAME = "getDKey";
	public static final String PLG_BUILD_CACHE_MTD_NAME = "buildCache";
	public static final String PLG_RECYCLE_MTD_NAME = "recycle";
	public static final String PLG_SUBMIT_MTD_PREFIX = "do";
	public static final String VLD_MSG_KEY_SUFFIX = "VldMsg";

	public static final String CACHE_TOWER_KEY = "ct";
	public static final String SESSION_ID_KEY = "sid";

	public static final String TITLE_KEY = "title";

}
