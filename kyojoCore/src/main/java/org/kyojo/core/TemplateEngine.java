package org.kyojo.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.Path.Node;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.StringEscapeUtils;
import org.kyojo.core.annotation.ArgsListIndex;
import org.kyojo.core.annotation.ArgsListNo;
import org.kyojo.core.annotation.InheritDefault;
import org.kyojo.core.annotation.InheritParent;
import org.kyojo.core.annotation.OutOfRequestData;
import org.kyojo.core.annotation.OutOfResponseData;
import org.kyojo.core.annotation.TrivialRequestData;
import org.kyojo.core.validation.ManualLocaleMessageInterpolator;
import org.kyojo.gson.JsonParseException;
import org.kyojo.gson.JsonSyntaxException;
import org.kyojo.gson.reflect.TypeToken;
import org.kyojo.minion.My;
import org.kyojo.plugin.markup.BracketsNode;
import org.kyojo.schemaorg.SchemaOrgProperty;
import org.kyojo.schemaorg.SimpleJsonBuilder;

public class TemplateEngine {

	private static final Log logger = LogFactory.getLog(TemplateEngine.class);

	public static final Pattern tppt1 = Pattern.compile("^([ \\t]*)(.+?)([\\r\\n]*)$");

	public static final Pattern tppt2 = Pattern.compile("^([#?!])([\\w/]+)([\\(\\{\\[].*?[\\)\\}\\],]|)$");

	public static final Pattern opnpt = Pattern.compile("^(<[a-z0-9]+><[a-z0-9]+><[a-z0-9]+>|<[a-z0-9]+><[a-z0-9]+>|<[a-z0-9]+>|)");

	public static final Pattern clspt = Pattern.compile("(</[a-z0-9]+></[a-z0-9]+></[a-z0-9]+>|</[a-z0-9]+></[a-z0-9]+>|</[a-z0-9]+>|)$");

	private GlobalData gbd = null;

	private SessionData ssd = null;

	private RequestData rqd = null;

	private ResponseData rpd = null;

	private Skin skin = null;

	private PluginManager pm = null;

	private static boolean isLogDebugCache = false;

	private static boolean isLogAllMagicWords = false;

	private static boolean isLogMagicWordPattern = false;

	private static boolean isLogMagicWordValues = false;

	private static boolean isLogMagicWordReplace = false;

	private static boolean isLogParseTemplete = false;

	private static boolean isLogSaveAndLoadResult = false;

	private static boolean isLogMagicTower = false;

	private static int MAGIC_FLOORS_LIMIT = 50;

	private static int magicFloorsThrough = 20;

	private static int magicListDepthLimit = 1;

	private static int minionDepthLimit = 5;

	private static boolean isToMinionSchemaOrgProperty = false;

	private LinkedList<Cache> cacheTower = new LinkedList<>();

	private String act;

	private String ext;

	private String path;

	private String sid;

	private String ct = "";

	private LinkedList<HashMap<String, ReferenceStructure>> magicTower = new LinkedList<>();

	private HashMap<String, ReferenceStructure> magicFloor = null;

	private HashMap<String, ReferenceStructure> currentFloor = null;

	private LinkedList<Pattern> ptrnTower = new LinkedList<>();

	private Pattern magicPtrn = null;

	private LinkedList<Object> pluginTower = new LinkedList<>();

	public static String AP1_PATTERN = "^([\\w/]+)\\.(\\w+)(|#\\w+)$";

	public static String AP2_PATTERN = "^([\\w/]+)\\.(\\w+)(|#\\w+)\\?([\\w&;=%]+)$";

	public static String AP3_PATTERN = "^([\\w/]+)\\.(\\w+)(|#\\w+)(/[\\w/]+)$";

	public static String AP4_PATTERN = "^([\\w/]+)\\.(\\w+)(|#\\w+)(/[\\w/]+)\\?([\\w&;=%]+)$";

	public static Pattern ap1pt = Pattern.compile(AP1_PATTERN);

	public static Pattern ap2pt = Pattern.compile(AP2_PATTERN);

	public static Pattern ap3pt = Pattern.compile(AP3_PATTERN);

	public static Pattern ap4pt = Pattern.compile(AP4_PATTERN);

	public StringBuilder debugCacheTree = new StringBuilder("debugCacheTree\n");

	private LinkedList<Boolean> submitTower = new LinkedList<>();

	private Boolean submitCalled = false; // フィールドリセットとループ防止

	private String defaultBr = "\n";

	private ResourceBundle rb;

	private Validator validator;

	private Map<Class<?>, Map<String, Method[]>> gsMtdsMapMap = new HashMap<>();

	private Map<Class<?>, Map<String, Field>> fldMapMap = new HashMap<>();

	private Map<Class<?>, Map<String, Method>> plgMtdsMapMap = new HashMap<>();

	public TemplateEngine(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		this.gbd = gbd;
		this.ssd = ssd;
		this.rqd = rqd;
		this.rpd = rpd;

		Object val = gbd.get("IS_LOG_DEBUG_CACHE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogDebugCache = true;
		}
		val = gbd.get("IS_LOG_ALL_MAGIC_WORDS");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogAllMagicWords = true;
		}
		val = gbd.get("IS_LOG_MAGIC_WORD_PATTERN");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogMagicWordPattern = true;
		}
		val = gbd.get("IS_LOG_MAGIC_WORD_VALUES");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogMagicWordValues = true;
		}
		val = gbd.get("IS_LOG_MAGIC_WORD_REPLACE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogMagicWordReplace = true;
		}
		val = gbd.get("IS_LOG_PARSE_TEMPLETE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogParseTemplete = true;
		}
		val = gbd.get("IS_LOG_SAVE_AND_LOAD_RESULT");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogSaveAndLoadResult = true;
		}
		val = gbd.get("IS_LOG_MAGIC_TOWER");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogMagicTower = true;
		}

		val = gbd.get("MAGIC_FLOORS_THROUGH");
		if(val != null) {
			try {
				int ival = Integer.parseInt(val.toString());
				if(ival > 0) {
					magicFloorsThrough = ival;
				}
			} catch(Exception ex) {}
		}

		val = gbd.get("MAGIC_LIST_DEPTH_LIMIT");
		if(val != null) {
			try {
				int ival = Integer.parseInt(val.toString());
				if(ival > 0) {
					magicListDepthLimit = ival;
				}
			} catch(Exception ex) {}
		}

		val = gbd.get("MINION_DEPTH_LIMIT");
		if(val != null) {
			try {
				int ival = Integer.parseInt(val.toString());
				if(ival > 0) {
					minionDepthLimit = ival;
				}
			} catch(Exception ex) {}
		}

		val = gbd.get("IS_TO_MINION_SCHEMA_ORG_PROPERTY");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isToMinionSchemaOrgProperty = true;
		}

		act = rqd.getRequest().getParameter(Constants.ACT_KEY);
		if(StringUtils.isBlank(act)) act = Constants.DEFAULT_ACT;
		ext = rqd.getRequest().getParameter(Constants.EXT_KEY);
		if(StringUtils.isBlank(ext)) ext = Constants.DEFAULT_EXT;
		path = rqd.getRequest().getParameter(Constants.PATH_KEY);
		if(path == null) path = "";
		sid = ssd.getSessionID();
		logger.debug(String.format("%s=\"%s\", %s=\"%s\", %s=\"%s\"",
				Constants.ACT_KEY, act, Constants.EXT_KEY, ext, Constants.PATH_KEY, path));
		skin = new Skin(gbd, ssd, ext);
		pm = PluginManager.getInstance(gbd);

		currentFloor = new HashMap<>();
		for(Map.Entry<String, Object> ent : gbd.entrySet()) {
			val = ent.getValue();
			if(val == null) {
				val = "";
			}
			if(String.class.isAssignableFrom(val.getClass())) {
				String key = My.constantize(ent.getKey());
				currentFloor.put(key, new ReferenceStructure(val.toString()));
			}
		}
		// ToDo: 今のところ役に立つ場面がない？
		// for(Map.Entry<String, Object> ent : ssd.entrySet()) {
		//	String key = My.constantize(ent.getKey());
		//	currentFloor.put(key, ent.getValue().toString());
		// }
		currentFloor.put(My.constantize(Constants.ACT_KEY), new ReferenceStructure(act));
		currentFloor.put(My.constantize(Constants.EXT_KEY), new ReferenceStructure(ext));
		currentFloor.put(My.constantize(Constants.PATH_KEY), new ReferenceStructure(path));
		currentFloor.put(My.constantize(Constants.SESSION_ID_KEY), new ReferenceStructure(sid));

		String gbdPkg = GlobalData.class.getPackage().getName();
		Locale locale = rqd.getRequest().getLocale();
		rb = ResourceBundle.getBundle(gbdPkg + ".Messages", locale);
		for(String key : rb.keySet()) {
			currentFloor.put(My.constantize(key), new ReferenceStructure(rb.getString(key)));
		}
		magicFloor = new HashMap<>(currentFloor);

		magicTower.add(currentFloor);
		magicPtrn = updateMagicPtrn();
		ptrnTower.add(magicPtrn);

		Configuration<?> config = Validation.byDefaultProvider().configure();
		MessageInterpolator interpolator = config.getDefaultMessageInterpolator();
		ValidatorFactory factory = config.messageInterpolator(new ManualLocaleMessageInterpolator(interpolator, locale))
				.buildValidatorFactory();
		validator = factory.getValidator();
	}

	public void printContent() throws IOException {
		Cache cache;
		try {
			cache = cacheParsedTemplate(Constants.TEMPLATE_ROOT, "", "", false, false, null);
		} catch(PluginException pe) {
			logger.error(pe.getMessage(), pe);
			HashMap<String, String> err = new HashMap<>();
			err.put("errorMessage", pe.getMessage());
			ssd.turnOver(err, Constants.ERROR_SKIN);
			StringBuilder sb = new StringBuilder(gbd.get("BASE_URI").toString());
			sb.append("/");
			sb.append(Constants.ERROR_SKIN);
			sb.append(".");
			sb.append(skin.getExt());
			rpd.getResponse().sendRedirect(sb.toString());
			return;
		} catch(RedirectThrowable rt) {
			Object rdctTo = rt.getRedirectTo();
			// Classはプラグインパスが複数になったので廃止したい
			// if(rdctTo instanceof Class) {
			//	rdctTo = StringUtils.uncapitalize(((Class<?>)rdctTo).getSimpleName()) + "." + skin.getExt();
			// }
			if(rdctTo instanceof String) {
				String ap = (String)rdctTo;
				StringBuilder sb = new StringBuilder();
				if(!ap.startsWith("http")) {
					sb.append(gbd.get("BASE_URI"));
					// sb.append(rqd.getRequest().getContextPath());
					if(!ap.startsWith("/")) {
						sb.append("/");
					}
				}
				sb.append(ap);
				logger.debug("redirect to " + sb.toString());
				rpd.getResponse().sendRedirect(sb.toString());
			} else if (rdctTo instanceof URI) {
				logger.debug("redirect to " + rdctTo.toString());
				rpd.getResponse().sendRedirect(rdctTo.toString());
			}
			return;
		} catch(CompleteThrowable ct) {
			logger.debug("CompleteThrowable caught.");
			return;
		}
		// String act = request.getParameter(Constants.ACT_KEY").toString());
		// if(!skin.hasSkin(act)) {
		//	act = Constants.DEFAULT_ACT").toString();
		// }
		// Cache cache = cacheParsedTemplate(act, "", "", false);
		PrintWriter out = rpd.getResponse().getWriter();
		String full = new String(cache.toString().getBytes("UTF-8"), "ISO-8859-1");
		out.print(full);
		if(isLogDebugCache) {
			logger.debug(debugCacheTree.toString());
		}
	}

	public void changeExt(String ext) {
		skin = new Skin(gbd, ssd, ext);
	}

	private void getPluginMethods(Class<?> cls, Map<String, Method> plgMtdsMap) {
		if(plgMtdsMapMap.containsKey(cls)) {
			plgMtdsMap.putAll(plgMtdsMapMap.get(cls));
			return;
		}

		for(Method mtd : cls.getMethods()) {
			if(mtd.getAnnotation(Deprecated.class) != null) {
				continue;
			}

			if(plgMtdsMap != null) {
				if(!plgMtdsMap.containsKey("inMtd") && mtd.getName().equals(Constants.PLG_INITIALIZE_MTD_NAME)
						&& isInitializeMethod(mtd)) {
					plgMtdsMap.put("inMtd", mtd);
				// } else if(!plgMtdsMap.containsKey("gdMtd") && mtd.getName().equals(Constants.PLG_GET_D_KEY_MTD_NAME)
				//		&& isInitializeMethod(mtd)) {
				//	plgMtdsMap.put("gdMtd", mtd);
				} else if(!plgMtdsMap.containsKey("bcMtd") && mtd.getName().equals(Constants.PLG_BUILD_CACHE_MTD_NAME)
						&& isBuildCacheMethod(mtd)) {
					plgMtdsMap.put("bcMtd", mtd);
				} else if(!plgMtdsMap.containsKey("rcMtd") && mtd.getName().equals(Constants.PLG_RECYCLE_MTD_NAME)
						&& isInitializeMethod(mtd)) {
					plgMtdsMap.put("rcMtd", mtd);
				} else {
					if(mtd.getName().startsWith(Constants.PLG_SUBMIT_MTD_PREFIX) && isBuildCacheMethod(mtd)
							&& rqd.containsKeyBfDlm(mtd.getName())) {
						plgMtdsMap.put("smMtd", mtd);
					}
				}
			}
		}
		plgMtdsMapMap.put(cls, plgMtdsMap);
	}

	public static List<Field> getAllFields(Class<?> type) {
		List<Field> flds = new ArrayList<>();
		for(Class<?> cls = type; cls != null; cls = cls.getSuperclass()) {
			flds.addAll(Arrays.asList(cls.getDeclaredFields()));
		}
		return flds;
	}

	public static void getMethodsAndFields(Class<?> cls,
			Map<String, Method[]> gsMtdsMap, Map<String, Field> fldMap,
			Map<Class<?>, Map<String, Method[]>> gsMtdsMapMap,
			Map<Class<?>, Map<String, Field>> fldMapMap) {
		boolean found = false;
		if(gsMtdsMapMap != null && gsMtdsMapMap.containsKey(cls)) {
			gsMtdsMap.putAll(gsMtdsMapMap.get(cls));
			found = true;
		}
		if(fldMapMap != null && fldMapMap.containsKey(cls)) {
			fldMap.putAll(fldMapMap.get(cls));
			found = true;
		}

		if(found) {
			return;
		}

		Method[] mtds = cls.getMethods();
		for(Method mtd : mtds) {
			if(mtd.getAnnotation(Deprecated.class) != null) {
				continue;
			}
			int modifier = mtd.getModifiers();
			if(Modifier.isFinal(modifier)
					|| Modifier.isStatic(modifier) || Modifier.isVolatile(modifier)) {
				continue;
			}

			if(mtd.getName().length() > 3
					&& ((mtd.getName().startsWith("get") && mtd.getParameterTypes().length == 0)
							|| (mtd.getName().startsWith("set") && mtd.getParameterTypes().length == 1))) {
				// get/setメソッドを記憶
				String klc = mtd.getName().substring(3).toLowerCase();
				if(!klc.equals("class") && !klc.equals("nativevalue")
						&& !klc.equals("property") && !klc.equals("metaclass")) {
					if(!gsMtdsMap.containsKey(klc)) {
						gsMtdsMap.put(klc, new Method[2]);
					}
					Method[] gsMtds = gsMtdsMap.get(klc);
					gsMtds[mtd.getName().startsWith("get") ? 0 : 1] = mtd;
				}
			}
		}
		if(gsMtdsMapMap != null) {
			gsMtdsMapMap.put(cls, gsMtdsMap);
		}

		List<Field> flds = getAllFields(cls);
		for(Field fld : flds) {
			String klc = fld.getName().toLowerCase();
			if(fldMap.containsKey(klc)) {
				// 下位クラスのフィールドを優先
				continue;
			}
			// groovyのアノテーション判定のため最初は入れる
			// if(gsMtdsMap.containsKey(klc)) {
			//	// get/setメソッド優先
			//	continue;
			// }

			if(fld.getAnnotation(Deprecated.class) != null) {
				continue;
			}
			int modifier = fld.getModifiers();
			if(Modifier.isFinal(modifier)
					|| Modifier.isStatic(modifier) || Modifier.isVolatile(modifier)) {
				continue;
			}

			if(!klc.equals("class") && !klc.equals("nativevalue")
					&& !klc.equals("property") && !klc.equals("metaclass")
					&& !klc.startsWith("_")) {
				// publicフィールドを記憶
				fldMap.put(klc, fld);
			}
		}
		if(fldMapMap != null) {
			fldMapMap.put(cls, fldMap);
		}
	}

	private void logMagicWordValues() {
		if(magicPtrn == null) return;
		String ptrn = magicPtrn.pattern();
		if(ptrn.length() < 7) return;
		if(cacheTower.size() < 1) return;
		int floorNum = cacheTower.size();
		String sKey = cacheTower.getLast().getSKey();
		String[] mwks = ptrn.substring(3, ptrn.length() - 3).split("\\|");
		for(String mwk : mwks) {
			if(mwk.length() > 0) {
				if(magicFloor.containsKey(mwk)) {
					logger.debug("magicWord: " + floorNum + ", " + sKey + ", " + mwk + "="
							+ magicFloor.get(mwk).getMnn());
				} else {
					logger.debug("magicWord: " + floorNum + ", " + sKey + ", " + mwk + "=(null)");
				}
			}
		}
	}

	// ToDo: 可能なら通常のparseTemplateに移行して削除
	public String parseTemplate(String indent, String sKey, String args,
			String br, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		logger.trace("parseTemplate start: " + sKey);
		if(args == null) args = "";
		String dKey = "";
		if(StringUtils.isNotEmpty(args)) {
			dKey = My.hs(args);
		}
		Cache cache = new Cache(sKey, dKey, skin.getExt(), new Time14());

		boolean ctRaised = false;
		boolean mtRaised = false;
		raiseCacheTower(cache);
		ctRaised = true;
		raiseMagicTower();
		mtRaised = true;
		try {
			String args2 = args;
			if(args.length() > 0 && magicPtrn != null) {
				// magic word展開
				logger.trace("parseTemplate magicPtrn start: " + sKey);
				Matcher mc = magicPtrn.matcher(args);
				StringBuffer sb = new StringBuffer();
				while(mc.find()) {
					String mwk = mc.group(1);
					ReferenceStructure ref = magicFloor.get(mwk);
					if(mwk.equals(My.constantize(Constants.MINION_SUFFIX))
							|| ReferenceConverter.convKey2Suf(mwk).equals(Constants.MINION_SUFFIX)) {
						if(ref == null) {
							if(isLogMagicWordReplace) {
								logger.debug("replace: " + mwk + "->(null)");
							}
							mc.appendReplacement(sb, "null");
						} else {
							String mwv = ref.getMnn();
							if(isLogMagicWordReplace) {
								logger.debug("replace: " + mwk + "->" + mwv);
							}
							mc.appendReplacement(sb, Matcher.quoteReplacement(mwv));
						}
					} else {
						if(ref == null) {
							if(isLogMagicWordReplace) {
								logger.debug("replace: " + mwk + "->(null)");
							}
							mc.appendReplacement(sb, "");
						} else {
							String mwv = ref.conv(String.class).getObj();
							if(isLogMagicWordReplace) {
								logger.debug("replace: " + mwk + "->" + mwv);
							}
							mc.appendReplacement(sb, Matcher.quoteReplacement(mwv));
						}
					}
				}
				mc.appendTail(sb);
				args2 = sb.toString();
				logger.trace("parseTemplate magicPtrn end: " + sKey);
			}

			parseTemplate(cache, indent, "#", sKey, args2, br, isForced);
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if(ctRaised) reduceCacheTower();
			if(mtRaised) reduceMagicTower();
		}

		Iterator<String> itr = cache.getLines();
		StringBuilder sb = new StringBuilder();
		while(itr.hasNext()) {
			sb.append(itr.next());
		}
		logger.trace("parseTemplate end: " + sKey);
		return sb.toString();
	}

	// ToDo: 可能なら通常のparseTemplateに移行して削除
	public void parseTemplate(Cache parent, String indent, String sKey, String args, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		logger.trace("parseTemplate start: " + sKey);
		if(args == null) args = "";
		String dKey = "";
		if(StringUtils.isNotEmpty(args)) {
			dKey = My.hs(args);
		}
		Cache cache = new Cache(sKey, dKey, skin.getExt(), new Time14());

		boolean ctRaised = false;
		boolean mtRaised = false;
		raiseCacheTower(cache);
		ctRaised = true;
		raiseMagicTower();
		mtRaised = true;
		try {
			parseTemplate(cache, indent, "#", sKey, args, defaultBr, isForced);
		} finally {
			if(ctRaised) reduceCacheTower();
			if(mtRaised) reduceMagicTower();
		}

		Iterator<String> itr = cache.getLines();
		while(itr.hasNext()) {
			parent.addLine(itr.next());
		}
		logger.trace("parseTemplate end: " + sKey);
	}

	public void parseTemplate(Cache parent, String indent, String type, String sKey, String args,
			String br, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		logger.trace("parseTemplate start: " + sKey);
		if(isLogParseTemplete) {
			if(args.equals("null")) {
				logger.debug("parsing: " + indent + type + sKey + "(null)");
			} else {
				logger.debug("parsing: " + indent + type + sKey + args);
			}
		}
		if(cacheTower.size() > MAGIC_FLOORS_LIMIT) {
			logger.warn("floors exceeded.");
			logger.trace("parseTemplate end: " + sKey);
			return;
		}

		int len = args.length();
		String args0 = args;
		boolean isMapArgs = false;
		boolean isListMapArgs = false;
		if(args.startsWith("(")) {
			args = args.substring(1, len - 1);
		} else if(args.startsWith("{")) {
			isMapArgs = true;
		} else if(args.startsWith("[")) {
			isListMapArgs = true;
		}

		String dKey = "";
		if(StringUtils.isNotEmpty(args)) {
			dKey = My.hs(args);
		}
		String ssdKey = Cache.concatKeys(sKey, dKey, "mnn");
		if(type.equals("!")) {
			// TODO: finally あるいは テスト対象 必要？
		} else if(type.equals("?")) {
			parent.setExpires(Time14.OLD);
			if(act != null && (act.equals(sKey) || act.endsWith("/" + sKey))) {
				parent.setSkipAfter(true);
			} else {
				logger.trace("parseTemplate end: " + sKey);
				return;
			}
		}
		defaultBr = br;

		boolean hasPlugin = false;
		Object obj = null;
		HashMap<String, Method> plgMtdsMap = new HashMap<>();
		HashMap<String, Method[]> gsMtdsMap = new HashMap<>();
		HashMap<String, Field> fldMap = new HashMap<>();
		Map<String, Boolean> resultFlgs = new HashMap<>();
		resultFlgs.put("pluginRan", false);
		resultFlgs.put("templateParsed", false);
		if(type.equals("#") || type.equals("?")) {
			boolean mtRaised = false;
			try {
				raiseMagicTower();
				mtRaised = true;

				logger.trace("parseTemplate loadPlugin start: " + act);
				Class<?> cls = pm.loadPlugin(act, sKey, ssd);
				logger.trace("parseTemplate loadPlugin end: " + act);
				if(cls != null) {
					hasPlugin = true;

					// メソッドとフィールドの情報を収集
					logger.trace("parseTemplate getPluginMethods start: " + cls);
					getPluginMethods(cls, plgMtdsMap);
					logger.trace("parseTemplate getPluginMethods end: " + cls);
					logger.trace("parseTemplate getMethodsAndFields start: " + cls);
					getMethodsAndFields(cls, gsMtdsMap, fldMap, gsMtdsMapMap, fldMapMap);
					logger.trace("parseTemplate getMethodsAndFields end: " + cls);

					// submitの場合、あればnameからpathを取得
					if(!submitCalled) {
						if(plgMtdsMap.containsKey("smMtd")) {
							Method smMtd = plgMtdsMap.get("smMtd");
							if(rqd.containsKeyBfDlm(smMtd.getName())) {
								path = rqd.getKeyAfDlm(smMtd.getName());
							}
						}
						if(path == null) {
							path = "";
						}

						for(HashMap<String, ReferenceStructure> tmpFloor : magicTower) {
							tmpFloor.put(My.constantize(Constants.PATH_KEY), new ReferenceStructure(path));
						}
					}

					Cache mc = null;
					boolean needInit = false;
					if(!isMapArgs && !isListMapArgs
							&& (gsMtdsMap.size() > 0 || fldMap.size() > 0)) {
						// stateless
						// ssdに前回アクセス時のデータがあるか
						mc = (Cache)ssd.get(ssdKey);
						if(mc == null) {
							if(isLogSaveAndLoadResult) {
								logger.debug("ssd-load: " + ssdKey + " -> (no data)");
							}
						}
					} else {
						// stateful
						if(isLogSaveAndLoadResult) {
							logger.debug("ssd-load: " + ssdKey + " -> (skipped)");
						}
					}
					if(mc == null) {
						// ない
						needInit = true;
					} else {
						// プラグインファイルの更新時刻と比較
						logger.trace("parseTemplate getPluginFile start: " + sKey);
						File pf = pm.getPluginFile(act, sKey, null, ssd);
						logger.trace("parseTemplate getPluginFile end: " + sKey);
						long lm = pf.lastModified();
						if(lm == 0L) {
							needInit = true;
							if(isLogSaveAndLoadResult) {
								logger.debug("ssd-load: " + ssdKey + " -> (error?)");
							}
						} else {
							Time14 lmT14 = new Time14(new Date(lm));
							if(mc.getCreated().compareTo(lmT14) < 0) {
								needInit = true;
								if(isLogSaveAndLoadResult) {
									logger.debug("ssd-load: " + ssdKey + " -> (expired)");
								}
							}
						}
					}

					if(needInit) {
						// なければインスタンス生成
						logger.trace("parseTemplate newInstance start: " + cls);
						obj = cls.newInstance();
						logger.trace("parseTemplate newInstance end: " + cls);
					} else {
						Iterator<String> itr = mc.getLines();
						String mnn = null;
						if(itr.hasNext()) {
							mnn = itr.next();
						}

						if(isLogSaveAndLoadResult) {
							logger.debug("ssd-load: " + ssdKey + " -> " + mnn);
						}
						try {
							obj = My.deminion(mnn, cls);
							resultFlgs.put("pluginRan", true);
						} catch(JsonSyntaxException jse) {
							logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
							logger.warn("class: " + cls.getName());
							logger.warn(jse.getMessage(), jse);
							obj = cls.newInstance();
							needInit = true;
						} catch(JsonParseException jpe) {
							logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
							logger.warn("class: " + cls.getName());
							logger.warn(jpe.getMessage(), jpe);
							obj = cls.newInstance();
							needInit = true;
						} catch(Exception ex) {
							logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
							logger.warn("class: " + cls.getName());
							logger.warn(ex.getMessage(), ex);
							obj = cls.newInstance();
							needInit = true;
						}
					}
					pluginTower.add(obj);

					if(needInit && plgMtdsMap.containsKey("inMtd")) {
						Object res;
						try {
							final Method inMtd = plgMtdsMap.get("inMtd");
							logger.trace("parseTemplate initialize start: " + sKey);
							res = inMtd.invoke(obj, args, gbd, ssd, rqd, rpd);
							logger.trace("parseTemplate initialize end: " + sKey);
							if(rpd.getResponse().isCommitted()) {
								throw new CompleteThrowable();
							}
						} catch(InvocationTargetException ite) {
							Throwable cause = ite.getCause();
							if(cause == null) {
								throw ite;
							} else if(cause instanceof RedirectThrowable) {
								RedirectThrowable rt = (RedirectThrowable)cause;
								res = rt.getRedirectTo();
							} else if(cause instanceof CompleteThrowable) {
								throw (CompleteThrowable)cause;
							} else if(cause instanceof PluginException) {
								throw (PluginException)cause;
							} else {
								throw ite;
							}
						}

						resultFlgs.put("pluginRan", true);
						if(isRedirectable(res)) {
							logger.info("throwing RedirectThrowable: " + res);
							throw new RedirectThrowable(res);
						} else {
							updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
						}
					} else if(plgMtdsMap.containsKey("rcMtd")) {
						Object res;
						try {
							final Method rcMtd = plgMtdsMap.get("rcMtd");
							logger.trace("parseTemplate recycle start: " + sKey);
							res = rcMtd.invoke(obj, args, gbd, ssd, rqd, rpd);
							logger.trace("parseTemplate recycle end: " + sKey);
							if(rpd.getResponse().isCommitted()) {
								throw new CompleteThrowable();
							}
						} catch(InvocationTargetException ite) {
							Throwable cause = ite.getCause();
							if(cause == null) {
								throw ite;
							} else if(cause instanceof RedirectThrowable) {
								RedirectThrowable rt = (RedirectThrowable)cause;
								res = rt.getRedirectTo();
							} else if(cause instanceof CompleteThrowable) {
								throw (CompleteThrowable)cause;
							} else if(cause instanceof PluginException) {
								throw (PluginException)cause;
							} else {
								throw ite;
							}
						}

						resultFlgs.put("pluginRan", true);
						if(isRedirectable(res)) {
							logger.info("throwing RedirectThrowable: " + res);
							throw new RedirectThrowable(res);
						} else {
							updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
						}
					} else {
						resultFlgs.put("pluginRan", true);
					}

					// rqdが存在する場合はメソッドとフィールドを上書き
					boolean prmFound = false;
					Set<Method> inheritDefaultMtds = null;
					Set<Field> inheritDefaultFlds = null;
					Set<Method> inheritParentMtds = null;
					Set<Field> inheritParentFlds = null;
					Map<String, Method[]> notFoundMtdsMap = new HashMap<>();
					Map<String, Field> notFoundFldsMap = new HashMap<>();
					if(!isMapArgs && !isListMapArgs) {
						inheritDefaultMtds = new HashSet<>();
						inheritDefaultFlds = new HashSet<>();
						inheritParentMtds = new HashSet<>();
						inheritParentFlds = new HashSet<>();

						for(Map.Entry<String, Method[]> ent : gsMtdsMap.entrySet()) {
							// メソッド
							Method[] gsMtds = ent.getValue();
							boolean found = false;
							boolean isTrivial = false;
							if(gsMtds[0] != null && gsMtds[1] != null) {
								Method sm = gsMtds[1];
								Field fld = fldMap.get(ent.getKey());
								if(sm.isAnnotationPresent(OutOfRequestData.class)) {
									continue;
								} else if(sm.isAnnotationPresent(TrivialRequestData.class)) {
									isTrivial = true;
								} else if(sm.isAnnotationPresent(InheritDefault.class)) {
									inheritDefaultMtds.add(sm);
									continue;
								} else if(sm.isAnnotationPresent(InheritParent.class)) {
									inheritParentMtds.add(sm);
									continue;
								} else if(fld != null) {
									if(fld.isAnnotationPresent(OutOfRequestData.class)) {
										continue;
									} else if(fld.isAnnotationPresent(TrivialRequestData.class)) {
										isTrivial = true;
									} else if(fld.isAnnotationPresent(InheritDefault.class)) {
										inheritDefaultMtds.add(sm);
										continue;
									} else if(fld.isAnnotationPresent(InheritParent.class)) {
										inheritParentMtds.add(sm);
										continue;
									}
								}

								String kpc = StringUtils.uncapitalize(sm.getName().substring(3));
								ParamSidePack psp = ParamSidePack.byConversionClass(gsMtds, fld, kpc, rpd);

								if(rqd.containsKey(kpc)) {
									// rqdが存在すれば上書き
									Class<?>[] prmClss = sm.getParameterTypes();
									if(prmClss.length == 1) {
										Object ro = rqd.get(kpc);
										if(ro instanceof ReferenceStructure) {
											ReferenceStructure rs = (ReferenceStructure)ro;
											psp = ParamSidePack.byConversionReference(rs, psp, kpc, rpd);
											if(List.class.isAssignableFrom(prmClss[0])) {
												ParameterizedType gType = (ParameterizedType)sm.getGenericParameterTypes()[0];
												Type[] aTypes = gType.getActualTypeArguments();
												Class<?> aClass = (Class<?>)aTypes[0];
												if(!ParamSidePack.hasConversionClass(psp)) {
													ReferenceEntry<?> re = rs.conv(aClass);
													List<?> list;
													if(aClass.isArray() && aClass.getComponentType().equals(byte.class)) {
														list = re.getRawList();
													} else if(FileItem.class.isAssignableFrom(aClass)) {
														list = re.getFileItemList();
													} else {
														list = createListAndCopyFlx(aClass, re.getList(), re.getCls(), psp, kpc + ".");
													}
													sm.invoke(obj, list);
												} else {
													ReferenceEntry<String> re = rs.conv(String.class);
													ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), aClass);
													List<Object> list = new ArrayList<>();
													for(String str : re.getList()) {
														try {
															Object val = cub.convert(str, aClass);
															list.add(val);
														} catch(ConversionException ce) {
															logger.info(ce.getMessage());
															list.add(null);
														}
													}
													sm.invoke(obj, list);
												}
											} else {
												if(!ParamSidePack.hasConversionClass(psp)) {
													ReferenceEntry<?> re = rs.conv(prmClss[0]);
													if(prmClss[0].isArray() && prmClss[0].getComponentType().equals(byte.class)) {
														sm.invoke(obj, re.getRaw());
													} else if(FileItem.class.isAssignableFrom(prmClss[0])) {
														sm.invoke(obj, re.getFileItem());
													} else {
														Object val = createObjectAndCopyFlx(prmClss[0], re.getObj(), re.getCls(), psp, kpc + ".");
														sm.invoke(obj, val);
													}
												} else {
													ReferenceEntry<String> re = rs.conv(String.class);
													ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), prmClss[0]);
													try {
														Object val = cub.convert(re.getObj(), prmClss[0]);
														sm.invoke(obj, val);
													} catch(ConversionException ce) {
														logger.info(ce.getMessage());
														sm.invoke(obj, new Object[] { null });
													}
												}
											}
											found = true;
										} else if(ro instanceof Map) {
											if(List.class.isAssignableFrom(prmClss[0])) {
												ParameterizedType gType = (ParameterizedType)sm.getGenericParameterTypes()[0];
												Type[] aTypes = gType.getActualTypeArguments();
												List<Object> list = createListAndCopyFlx((Class<?>)aTypes[0], ro, Map.class, psp, kpc + ".");
												sm.invoke(obj, list);
											} else {
												Object val = createObjectAndCopyFlx(prmClss[0], ro, Map.class, psp, kpc + ".");
												sm.invoke(obj, val);
											}
											found = true;
										} else if(ro instanceof List) {
											if(List.class.isAssignableFrom(prmClss[0])) {
												ParameterizedType gType = (ParameterizedType)sm.getGenericParameterTypes()[0];
												Type[] aTypes = gType.getActualTypeArguments();
												if(!ParamSidePack.hasConversionClass(psp)) {
													List<?> list = createListAndCopyFlx((Class<?>)aTypes[0], ro, List.class, psp, kpc + ".");
													sm.invoke(obj, list);
												} else {
													List<String> list1 = createListAndCopyFlx(String.class, ro, List.class, psp, kpc + ".");
													ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), (Class<?>)aTypes[0]);
													List<Object> list2 = new ArrayList<>();
													for(String str : list1) {
														try {
															Object val = cub.convert(str, (Class<?>)aTypes[0]);
															list2.add(val);
														} catch(ConversionException ce) {
															logger.info(ce.getMessage());
															list2.add(null);
														}
													}
													sm.invoke(obj, list2);
												}
											} else {
												if(!ParamSidePack.hasConversionClass(psp)) {
													List<?> list = createListAndCopyFlx(prmClss[0], ro, List.class, psp, kpc + ".");
													if(list.size() > 0) {
														sm.invoke(obj, list.get(0));
													}
												} else {
													List<String> list = createListAndCopyFlx(String.class, ro, List.class, psp, kpc + ".");
													if(list.size() > 0 && list.get(0) != null) {
														String val1 = list.get(0);
														ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), prmClss[0]);
														try {
															Object val2 = cub.convert(val1, prmClss[0]);
															sm.invoke(obj, val2);
														} catch(ConversionException ce) {
															logger.info(ce.getMessage());
															sm.invoke(obj, new Object[] { null });
														}
													}
												}
											}
											found = true;
										}
									}
								}
								if(found) {
									if(!isTrivial) {
										prmFound = true;
									}
								} else {
									notFoundMtdsMap.put(ent.getKey(), ent.getValue());
								}
							}
						}
						for(Map.Entry<String, Field> ent : fldMap.entrySet()) {
							// フィールド
							boolean found = false;
							boolean isTrivial = false;
							if(gsMtdsMap.containsKey(ent.getKey())) {
								continue;
							}

							Field fld = ent.getValue();
							if(fld.isAnnotationPresent(OutOfRequestData.class)) {
								continue;
							} else if(fld.isAnnotationPresent(TrivialRequestData.class)) {
								isTrivial = true;
							} else if(fld.isAnnotationPresent(InheritDefault.class)) {
								inheritDefaultFlds.add(fld);
								continue;
							} else if(fld.isAnnotationPresent(InheritParent.class)) {
								inheritParentFlds.add(fld);
								continue;
							}

							String kcm = fld.getName();
							ParamSidePack psp = ParamSidePack.byConversionClass(fld, kcm, rpd);

							if(rqd.containsKey(kcm)) {
								// rqdが存在すれば上書き
								Object ro = rqd.get(kcm);
								if(ro instanceof ReferenceStructure) {
									ReferenceStructure rs = (ReferenceStructure)ro;
									psp = ParamSidePack.byConversionReference(rs, psp, kcm, rpd);
									if(List.class.isAssignableFrom(fld.getType())) {
										ParameterizedType gType = (ParameterizedType)fld.getGenericType();
										Type[] aTypes = gType.getActualTypeArguments();
										Class<?> aClass = (Class<?>)aTypes[0];
										if(!ParamSidePack.hasConversionClass(psp)) {
											ReferenceEntry<?> re = rs.conv(aClass);
											List<?> list;
											if(aClass.isArray() && aClass.getComponentType().equals(byte.class)) {
												list = re.getRawList();
											} else if(FileItem.class.isAssignableFrom(aClass)) {
												list = re.getFileItemList();
											} else {
												list = createListAndCopyFlx(aClass, re.getList(), re.getCls(), psp, kcm + ".");
											}
											fld.setAccessible(true);
											fld.set(obj, list);
										} else {
											ReferenceEntry<String> re = rs.conv(String.class);
											ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), aClass);
											List<Object> list = new ArrayList<>();
											for(String str : re.getList()) {
												try {
													Object val = cub.convert(str, aClass);
													list.add(val);
												} catch(ConversionException ce) {
													logger.info(ce.getMessage());
													list.add(null);
												}
											}
											fld.setAccessible(true);
											fld.set(obj, list);
										}
									} else {
										if(!ParamSidePack.hasConversionClass(psp)) {
											ReferenceEntry<?> re = rs.conv(fld.getType());
											if(fld.getType().isArray() && fld.getType().getComponentType().equals(byte.class)) {
												fld.setAccessible(true);
												fld.set(obj, re.getRaw());
											} else if(FileItem.class.isAssignableFrom(fld.getType())) {
												fld.setAccessible(true);
												fld.set(obj, re.getFileItem());
											} else {
												Object val = createObjectAndCopyFlx(fld.getType(), re.getObj(), re.getCls(), psp, kcm + ".");
												fld.setAccessible(true);
												fld.set(obj, val);
											}
										} else {
											ReferenceEntry<String> re = rs.conv(String.class);
											ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), fld.getType());
											try {
												Object val = cub.convert(re.getObj(), fld.getType());
												fld.setAccessible(true);
												fld.set(obj, val);
											} catch(ConversionException ce) {
												logger.info(ce.getMessage());
												fld.set(obj, null);
											}
										}
									}
									found = true;
								} else if(ro instanceof Map) {
									if(List.class.isAssignableFrom(fld.getType())) {
										ParameterizedType gType = (ParameterizedType)fld.getGenericType();
										Type[] aTypes = gType.getActualTypeArguments();
										List<Object> list = createListAndCopyFlx((Class<?>)aTypes[0], ro, Map.class, psp, kcm + ".");
										fld.setAccessible(true);
										fld.set(obj, list);
									} else {
										Object val = createObjectAndCopyFlx(fld.getType(), ro, Map.class, psp, kcm + ".");
										fld.setAccessible(true);
										fld.set(obj, val);
									}
									found = true;
								} else if(ro instanceof List) {
									if(List.class.isAssignableFrom(fld.getType())) {
										ParameterizedType gType = (ParameterizedType)fld.getGenericType();
										Type[] aTypes = gType.getActualTypeArguments();
										if(!ParamSidePack.hasConversionClass(psp)) {
											List<?> list = createListAndCopyFlx((Class<?>)aTypes[0], ro, List.class, psp, kcm + ".");
											fld.setAccessible(true);
											fld.set(obj, list);
										} else {
											List<String> list1 = createListAndCopyFlx(String.class, ro, List.class, psp, kcm + ".");
											ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), (Class<?>)aTypes[0]);
											List<Object> list2 = new ArrayList<>();
											for(String str : list1) {
												try {
													Object val = cub.convert(str, (Class<?>)aTypes[0]);
													list2.add(val);
												} catch(ConversionException ce) {
													logger.info(ce.getMessage());
													list2.add(null);
												}
											}
											fld.setAccessible(true);
											fld.set(obj, list2);
										}
									} else {
										if(!ParamSidePack.hasConversionClass(psp)) {
											List<?> list = createListAndCopyFlx(fld.getType(), ro, List.class, psp, kcm + ".");
											if(list.size() > 0) {
												fld.setAccessible(true);
												fld.set(obj, list.get(0));
											}
										} else {
											List<String> list = createListAndCopyFlx(String.class, ro, List.class, psp, kcm + ".");
											if(list.size() > 0 && list.get(0) != null) {
												String val1 = list.get(0);
												ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), fld.getType());
												try {
													Object val2 = cub.convert(val1, fld.getType());
													fld.setAccessible(true);
													fld.set(obj, val2);
												} catch(ConversionException ce) {
													logger.info(ce.getMessage());
													fld.set(obj, null);
												}
											}
										}
									}
									found = true;
								}
							}
							if(found) {
								if(!isTrivial) {
									prmFound = true;
								}
							} else {
								notFoundFldsMap.put(ent.getKey(), ent.getValue());
							}
						}
					}

					// submitの場合、選択されていないチェックボックス等のためのリセット
					if(submitCalled || plgMtdsMap.containsKey("smMtd")
							|| (prmFound && !rqd.isAjax())) {
						for(Map.Entry<String, Method[]> ent : notFoundMtdsMap.entrySet()) {
							Method[] gsMtds = ent.getValue();
							Method gm = gsMtds[0];
							Method sm = gsMtds[1];
							Class<?> gmType = gm.getReturnType();
							if(boolean.class.isAssignableFrom(gmType)) {
								sm.invoke(obj, new Object[] { false });
							} else if(byte.class.isAssignableFrom(gmType)
									|| char.class.isAssignableFrom(gmType)
									|| short.class.isAssignableFrom(gmType)
									|| int.class.isAssignableFrom(gmType)
									|| long.class.isAssignableFrom(gmType)
									|| float.class.isAssignableFrom(gmType)
									|| double.class.isAssignableFrom(gmType)) {
								sm.invoke(obj, new Object[] { 0 });
							} else {
								sm.invoke(obj, new Object[] { null });
							}
						}
						for(Map.Entry<String, Field> ent : notFoundFldsMap.entrySet()) {
							Field fld = ent.getValue();
							Class<?> fldType = fld.getType();
							fld.setAccessible(true);
							if(boolean.class.isAssignableFrom(fldType)) {
								fld.set(obj, false);
							} else if(byte.class.isAssignableFrom(fldType)
									|| char.class.isAssignableFrom(fldType)
									|| short.class.isAssignableFrom(fldType)
									|| int.class.isAssignableFrom(fldType)
									|| long.class.isAssignableFrom(fldType)
									|| float.class.isAssignableFrom(fldType)
									|| double.class.isAssignableFrom(fldType)) {
								fld.set(obj, 0);
							} else {
								fld.set(obj, null);
							}
						}

						if(!needInit) {
							logger.trace("parseTemplate validate start: " + sKey);
							validate(obj, cls);
							logger.trace("parseTemplate validate end: " + sKey);
						}
					}

					if(inheritDefaultMtds == null) {
						inheritDefaultMtds = new HashSet<>();
						inheritDefaultFlds = new HashSet<>();
						inheritParentMtds = new HashSet<>();
						inheritParentFlds = new HashSet<>();
						for(Map.Entry<String, Method[]> ent : gsMtdsMap.entrySet()) {
							Method[] gsMtds = ent.getValue();
							if(gsMtds[0] != null && gsMtds[1] != null) {
								Method sm = gsMtds[1];
								Field fld = fldMap.get(ent.getKey());
								if(sm.isAnnotationPresent(InheritDefault.class)) {
									inheritDefaultMtds.add(sm);
								} else if(sm.isAnnotationPresent(InheritParent.class)) {
									inheritParentMtds.add(sm);
								} else if(fld != null) {
									if(fld.isAnnotationPresent(InheritDefault.class)) {
										inheritDefaultMtds.add(sm);
									} else if(fld.isAnnotationPresent(InheritParent.class)) {
										inheritParentMtds.add(sm);
									}
								}
							}
						}
						for(Map.Entry<String, Field> ent : fldMap.entrySet()) {
							Field fld = ent.getValue();
							if(fld.isAnnotationPresent(InheritDefault.class)) {
								inheritDefaultFlds.add(fld);
								continue;
							} else if(fld.isAnnotationPresent(InheritParent.class)) {
								inheritParentFlds.add(fld);
								continue;
							}
						}
					}

					HashMap<String, ReferenceStructure> defaultFloor = magicTower.get(0);
					for(Method mtd : inheritDefaultMtds) {
						String kpc = mtd.getName().substring(3);
						String kc = My.constantize(kpc);
						if(defaultFloor.containsKey(kc)) {
							ReferenceStructure rs = defaultFloor.get(kc);
							Class<?>[] prmClss = mtd.getParameterTypes();
							if(List.class.isAssignableFrom(prmClss[0])) {
								ParameterizedType gType = (ParameterizedType)mtd.getGenericParameterTypes()[0];
								Type[] aTypes = gType.getActualTypeArguments();
								Class<?> aClass = (Class<?>)aTypes[0];
								ReferenceEntry<?> re = rs.conv(aClass);
								mtd.invoke(obj, re.getList());
							} else {
								ReferenceEntry<?> re = rs.conv(prmClss[0]);
								mtd.invoke(obj, re.getObj());
							}
						}
					}
					for(Field fld: inheritDefaultFlds) {
						String kcm = fld.getName();
						String kc = My.constantize(kcm);
						if(defaultFloor.containsKey(kc)) {
							ReferenceStructure rs = defaultFloor.get(kc);
							if(List.class.isAssignableFrom(fld.getType())) {
								ParameterizedType gType = (ParameterizedType)fld.getGenericType();
								Type[] aTypes = gType.getActualTypeArguments();
								Class<?> aClass = (Class<?>)aTypes[0];
								ReferenceEntry<?> re = rs.conv(aClass);
								fld.setAccessible(true);
								fld.set(obj, re.getList());
							} else {
								ReferenceEntry<?> re = rs.conv(fld.getType());
								fld.setAccessible(true);
								fld.set(obj, re.getObj());
							}
						}
					}

					HashMap<String, ReferenceStructure> parentFloor = magicTower.get(magicTower.size() - 2);
					for(Method mtd : inheritParentMtds) {
						String kpc = mtd.getName().substring(3);
						String kc = My.constantize(kpc);
						if(parentFloor.containsKey(kc)) {
							ReferenceStructure rs = parentFloor.get(kc);
							Class<?>[] prmClss = mtd.getParameterTypes();
							if(List.class.isAssignableFrom(prmClss[0])) {
								ParameterizedType gType = (ParameterizedType)mtd.getGenericParameterTypes()[0];
								Type[] aTypes = gType.getActualTypeArguments();
								Class<?> aClass = (Class<?>)aTypes[0];
								ReferenceEntry<?> re = rs.conv(aClass);
								mtd.invoke(obj, re.getList());
							} else {
								ReferenceEntry<?> re = rs.conv(prmClss[0]);
								mtd.invoke(obj, re.getObj());
							}
						}
					}
					for(Field fld: inheritParentFlds) {
						String kcm = fld.getName();
						String kc = My.constantize(kcm);
						if(parentFloor.containsKey(kc)) {
							ReferenceStructure rs = parentFloor.get(kc);
							if(List.class.isAssignableFrom(fld.getType())) {
								ParameterizedType gType = (ParameterizedType)fld.getGenericType();
								Type[] aTypes = gType.getActualTypeArguments();
								Class<?> aClass = (Class<?>)aTypes[0];
								ReferenceEntry<?> re = rs.conv(aClass);
								fld.setAccessible(true);
								fld.set(obj, re.getList());
							} else {
								ReferenceEntry<?> re = rs.conv(fld.getType());
								fld.setAccessible(true);
								fld.set(obj, re.getObj());
							}
						}
					}
				}

				if(isListMapArgs) {
					if(cls == null) {
						pieceTemplate(parent, null, sKey, dKey, indent, isForced, hasPlugin, resultFlgs);
					} else {
						// List型のargsが渡された場合、要素毎に繰り返してpluginにマッピング
						try {
							Field listIndexFld = null;
							Field listNoFld = null;
							for(Map.Entry<String, Field> ent : fldMap.entrySet()) {
								Field fld = ent.getValue();
								if(fld.isAnnotationPresent(ArgsListIndex.class)
										&& (Integer.class.isAssignableFrom(fld.getType())
												|| Long.class.isAssignableFrom(fld.getType())
												|| int.class.isAssignableFrom(fld.getType())
												|| long.class.isAssignableFrom(fld.getType())
												|| String.class.isAssignableFrom(fld.getType()))) {
									fld.setAccessible(true);
									listIndexFld = fld;
									break;
								} else if(fld.isAnnotationPresent(ArgsListNo.class)
										&& (Integer.class.isAssignableFrom(fld.getType())
												|| Long.class.isAssignableFrom(fld.getType())
												|| int.class.isAssignableFrom(fld.getType())
												|| long.class.isAssignableFrom(fld.getType())
												|| String.class.isAssignableFrom(fld.getType()))) {
									fld.setAccessible(true);
									listNoFld = fld;
									break;
								}
							}

							Type listType = TypeToken.getParameterized(List.class, cls).getType();
							List<Object> argsObjList = My.deminion(args, listType);
							for(int lidx = 0; lidx < argsObjList.size(); lidx++) {
								Object argsObj = argsObjList.get(lidx);
								BeanUtils.copyProperties(obj, argsObj);
								if(listIndexFld != null) {
									if(String.class.isAssignableFrom(listIndexFld.getType())) {
										listIndexFld.set(obj, "" + lidx);
									} else {
										listIndexFld.set(obj, lidx);
									}
								}
								if(listNoFld != null) {
									if(String.class.isAssignableFrom(listNoFld.getType())) {
										listNoFld.set(obj, "" + (lidx + 1));
									} else {
										listNoFld.set(obj, lidx + 1);
									}
								}

								updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);

								String args2 = SimpleJsonBuilder.toJson(argsObj, cls);
								String dKey2 = My.hs(args2);
								parseTemplateSub(parent, indent,
										sKey, dKey2, args2, br, isForced,
										obj, cls, plgMtdsMap, gsMtdsMap, fldMap,
										hasPlugin, resultFlgs, isMapArgs, isListMapArgs);
							}

							// if(!isForced) {
							//	gbd.completeCache(parent);
							// }
						} catch(JsonSyntaxException jse) {
							logger.warn("JsonSyntaxException: " + jse.getMessage());
							logger.warn("json: " + StringUtils.abbreviate(args, 1024));
							throw new PluginException(obj, cls, args,
								gbd, ssd, rqd, rpd, jse.getMessage(), jse,
								PluginException.Level.WARN);
						} catch(JsonParseException jpe) {
							logger.warn("JsonSyntaxException: " + jpe.getMessage());
							logger.warn("json: " + StringUtils.abbreviate(args, 1024));
							throw new PluginException(obj, cls, args,
								gbd, ssd, rqd, rpd, jpe.getMessage(), jpe,
								PluginException.Level.WARN);
						} catch(Exception ex) {
							throw new PluginException(obj, cls, args,
								gbd, ssd, rqd, rpd, ex.getMessage(), ex,
								PluginException.Level.WARN);
						}
					}
				} else {
					if(cls != null) {
						if(isMapArgs) {
							// Map型のargsが渡された場合、pluginにマッピング
							try {
								Object argsObj = My.deminion(args, cls);
								BeanUtils.copyProperties(obj, argsObj);
							} catch(JsonSyntaxException jse) {
								logger.warn("JsonSyntaxException: " + jse.getMessage());
								logger.warn("json: " + StringUtils.abbreviate(args, 1024));
								throw new PluginException(obj, cls, args,
									gbd, ssd, rqd, rpd, jse.getMessage(), jse,
									PluginException.Level.WARN);
							} catch(JsonParseException jpe) {
								logger.warn("JsonSyntaxException: " + jpe.getMessage());
								logger.warn("json: " + StringUtils.abbreviate(args, 1024));
								throw new PluginException(obj, cls, args,
									gbd, ssd, rqd, rpd, jpe.getMessage(), jpe,
									PluginException.Level.WARN);
							} catch(Exception ex) {
								throw new PluginException(obj, cls, args,
									gbd, ssd, rqd, rpd, ex.getMessage(), ex,
									PluginException.Level.WARN);
							}
						}

						updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
					}

					parseTemplateSub(parent, indent,
							sKey, dKey, args, br, isForced,
							obj, cls, plgMtdsMap, gsMtdsMap, fldMap,
							hasPlugin, resultFlgs, isMapArgs, isListMapArgs);
				}
			} catch(IllegalArgumentException iae) {
				logger.warn("sKey=" + sKey + ", args=" + args);
				logger.warn(iae.getMessage(), iae);
				parent.setExpires(Time14.OLD);
				ssd.remove(ssdKey);
			} catch(InstantiationException ie) {
				logger.warn("sKey=" + sKey + ", args=" + args);
				logger.warn(ie.getMessage(), ie);
				parent.setExpires(Time14.OLD);
				ssd.remove(ssdKey);
			} catch(IllegalAccessException iae) {
				logger.warn("sKey=" + sKey + ", args=" + args);
				logger.warn(iae.getMessage(), iae);
				parent.setExpires(Time14.OLD);
				ssd.remove(ssdKey);
			} catch(InvocationTargetException ite) {
				logger.warn("sKey=" + sKey + ", args=" + args);
				logger.warn(ite.getMessage(), ite);
				parent.setExpires(Time14.OLD);
				ssd.remove(ssdKey);
			} catch(IllegalStateException ise) {
				logger.warn("sKey=" + sKey + ", args=" + args);
				logger.warn(ise.getMessage(), ise);
				parent.setExpires(Time14.OLD);
				ssd.remove(ssdKey);
			} catch(ClassCastException cce) {
				logger.warn("sKey=" + sKey + ", args=" + args);
				logger.warn(cce.getMessage(), cce);
				parent.setExpires(Time14.OLD);
				ssd.remove(ssdKey);
			} catch(PluginException pe) {
				logger.warn("PluginException: sKey=" + sKey + ", args=" + args);
				parent.setExpires(Time14.OLD);
				ssd.remove(ssdKey);
				if(pe.getLevel().compareTo(PluginException.Level.WARN) <= 0) {
					logger.trace("parseTemplate end: " + sKey);
					throw pe;
				} else {
					logger.warn(pe.getMessage(), pe);
				}
			} finally {
				if(obj != null) {
					// 最後に記録
					if(!isMapArgs && !isListMapArgs && resultFlgs.get("pluginRan")
							&& (gsMtdsMap.size() > 0 || fldMap.size() > 0)) {
						// String mnn = My.minion(obj);
						logger.trace("parseTemplate finally toJson start: " + sKey);
						String mnn = SimpleJsonBuilder.toJson(obj);
						logger.trace("parseTemplate finally toJson end: " + sKey);
						Cache mc = new Cache(sKey, My.hs(args), "mnn", new Time14());
						mc.addLine(mnn);
						if(isLogSaveAndLoadResult) {
							logger.debug("ssd-save: " + mc.getKey() + " -> " + mnn);
						}
						ssd.put(mc.getKey(), mc);
					}

					pluginTower.removeLast();
				}

				if(mtRaised) reduceMagicTower();
			}

			if(obj != null || resultFlgs.get("templateParsed")) {
				logger.trace("parseTemplate end: " + sKey);
				return;
			}
		}

		logger.warn("couldn't parse plugin " + type + sKey + args0 + ".");
		parent.setExpires(Time14.OLD);
		logger.trace("parseTemplate end: " + sKey);
	}

	private void parseTemplateSub(Cache parent, String indent,
			String sKey, String dKey, String args, String br, boolean isForced,
			Object obj, Class<?> cls, HashMap<String, Method> plgMtdsMap,
			HashMap<String, Method[]> gsMtdsMap, HashMap<String, Field> fldMap,
			boolean hasPlugin, Map<String, Boolean> resultFlgs,
			boolean isMapArgs, boolean isListMapArgs)
			throws PluginException, RedirectThrowable, CompleteThrowable,
				IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logger.trace("parseTemplateSub loadCacheIfPossible start: " + sKey);
		Cache child = gbd.loadCacheIfPossible(sKey, dKey, skin.getExt(), true);
		logger.trace("parseTemplateSub loadCacheIfPossible end: " + sKey);
		if(child == null) {
			if(cls != null) {
				Time14 expires = args.length() > 0 && !isMapArgs && !isListMapArgs
						? Time14.OLD : Time14.FUTURE; // 引数が丸括弧の場合
				child = new Cache(sKey, dKey, skin.getExt(), new Time14(), expires);
				if(plgMtdsMap.containsKey("bcMtd") || (!submitCalled && plgMtdsMap.containsKey("smMtd"))) {
					pm.getPluginFile(act, sKey, child, ssd);
					if(!submitCalled && plgMtdsMap.containsKey("smMtd")) {
						submitCalled = true;
						Object res;
						try {
							final Method smMtd = plgMtdsMap.get("smMtd");
							logger.trace("parseTemplateSub submit start: " + sKey);
							res = smMtd.invoke(obj, child, args, gbd, ssd, rqd, rpd,
									this, indent, isForced);
							logger.trace("parseTemplateSub submit end: " + sKey);
							if(rpd.getResponse().isCommitted()) {
								throw new CompleteThrowable();
							}
						} catch(InvocationTargetException ite) {
							Throwable cause = ite.getCause();
							if(cause == null) {
								throw ite;
							} else if(cause instanceof RedirectThrowable) {
								RedirectThrowable rt = (RedirectThrowable)cause;
								res = rt.getRedirectTo();
							} else if(cause instanceof CompleteThrowable) {
								throw (CompleteThrowable)cause;
							} else if(cause instanceof PluginException) {
								throw (PluginException)cause;
							} else {
								throw ite;
							}
						}

						if(isRedirectable(res)) {
							logger.info("throwing RedirectThrowable: " + res);
							throw new RedirectThrowable(res);
						} else {
							updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
						}
					}
					if(plgMtdsMap.containsKey("bcMtd")) {
						Object res;
						try {
							final Method bcMtd = plgMtdsMap.get("bcMtd");
							logger.trace("parseTemplateSub buildCache start: " + sKey);
							res = bcMtd.invoke(obj, child, args, gbd, ssd, rqd, rpd,
									this, indent, isForced);
							logger.trace("parseTemplateSub buildCache end: " + sKey);
							if(rpd.getResponse().isCommitted()) {
								throw new CompleteThrowable();
							}
						} catch(InvocationTargetException ite) {
							Throwable cause = ite.getCause();
							if(cause == null) {
								throw ite;
							} else if(cause instanceof RedirectThrowable) {
								RedirectThrowable rt = (RedirectThrowable)cause;
								res = rt.getRedirectTo();
							} else if(cause instanceof CompleteThrowable) {
								throw (CompleteThrowable)cause;
							} else if(cause instanceof PluginException) {
								throw (PluginException)cause;
							} else {
								throw ite;
							}
						}

						if(isRedirectable(res)) {
							logger.info("throwing RedirectThrowable: " + res);
							throw new RedirectThrowable(res);
						} else {
							updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
						}
					}
				}
				pm.getPluginFile(act, sKey, child, ssd);

				if(isListMapArgs) {
					addDebugCacheTree(child, indent, "created(sub)");
				}
			}
		} else {
			// if(isListMapArgs) {
				addDebugCacheTree(child, indent, "loaded(sub)");
			// }
		}

		pieceTemplate(parent, child, sKey, dKey, indent, isForced, hasPlugin, resultFlgs);
	}

	// ToDo: appendParsedTemplateと同じでは
	public void appendPiecedTemplate(Cache parent, String sKey, String args, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable,
				IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(skin.hasSkin(act, sKey)) {
			String dKey = "";
			if(StringUtils.isNotEmpty(args)) {
				dKey = My.hs(args);
			}

			Cache child = cacheParsedTemplate(sKey, dKey, indent, false, isForced, new Time14());
			parent.addChildKey(child.getKey(), child.getExpires()); // ToDo: 必要？

			if(!isForced) {
				logger.trace("appendPiecedTemplate completeCache start: " + sKey);
				gbd.completeCache(child);
				logger.trace("appendPiecedTemplate completeCache end: " + sKey);
			}

			Iterator<String> itr = child.getLines();
			while(itr.hasNext()) {
				String line = itr.next();
				if(line.length() > 0) {
					StringBuilder sb = new StringBuilder();
					sb.append(indent);
					sb.append(line);
					parent.addLine(sb.toString());
				}
			}
		}
	}

	private void pieceTemplate(Cache parent, Cache child,
			String sKey, String dKey, String indent, boolean isForced,
			boolean hasPlugin, Map<String, Boolean> resultFlgs)
			throws PluginException, RedirectThrowable, CompleteThrowable,
				IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(skin.hasSkin(act, sKey)) {
			if(child == null || child.isEmpty()) {
				Cache tmpChild = cacheParsedTemplate(sKey, dKey, indent, true, isForced, null);
				if(child != null) {
					tmpChild.copyRefFiles(child);
					tmpChild.copyMissFiles(child);
				}
				child = tmpChild;
			}

			resultFlgs.put("templateParsed", true);
		}
		if(child == null) {
			logger.warn("no template " + sKey + ".");
		} else {
			parent.addChildKey(child.getKey(), child.getExpires());

			if(!isForced) {
				logger.trace("pieceTemplate completeCache start: " + sKey);
				gbd.completeCache(child);
				logger.trace("pieceTemplate completeCache end: " + sKey);
			}

			Iterator<String> itr = child.getLines();
			while(itr.hasNext()) {
				String line = itr.next();
				if(line.length() > 0) {
					StringBuilder sb = new StringBuilder();
					sb.append(indent);
					sb.append(line);
					parent.addLine(sb.toString());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void validate(Object obj, Class<T> cls) {
		Set<ConstraintViolation<T>> constraintViolations = validator.validate((T)obj);
		if(constraintViolations.size() >= 0) {
			Iterator<ConstraintViolation<T>> constraintViolationsItr = constraintViolations.iterator();
			while(constraintViolationsItr.hasNext()) {
				ConstraintViolation<T> violation = constraintViolationsItr.next();
				Iterator<Node> violationPropertyPathItr = violation.getPropertyPath().iterator();
				StringBuilder sb = new StringBuilder();
				while(violationPropertyPathItr.hasNext()) {
					Node node = violationPropertyPathItr.next();
					if(sb.length() > 0) {
						sb.append(".");
					}
					if(node.getIndex() != null) {
						sb.append(node.getIndex() + 1);
						sb.append(".");
					}
					sb.append(node.getName());
				}
				String key = sb.toString();
				ParamSidePack.byValidationMessage(violation.getMessage(), key, rpd);
			}
		}
	}

	private boolean isInitializeMethod(Method mtd) {
		Class<?>[] prmClss = mtd.getParameterTypes();
		if(prmClss.length != 5) return false;
		if(!prmClss[0].equals(String.class)) return false;
		if(!prmClss[1].equals(GlobalData.class)) return false;
		if(!prmClss[2].equals(SessionData.class)) return false;
		if(!prmClss[3].equals(RequestData.class)) return false;
		if(!prmClss[4].equals(ResponseData.class)) return false;
		return true;
	}

	private boolean isBuildCacheMethod(Method mtd) {
		Class<?>[] prmClss = mtd.getParameterTypes();
		if(prmClss.length != 9) return false;
		if(!prmClss[0].equals(Cache.class)) return false;
		if(!prmClss[1].equals(String.class)) return false;
		if(!prmClss[2].equals(GlobalData.class)) return false;
		if(!prmClss[3].equals(SessionData.class)) return false;
		if(!prmClss[4].equals(RequestData.class)) return false;
		if(!prmClss[5].equals(ResponseData.class)) return false;
		if(!prmClss[6].equals(TemplateEngine.class)) return false;
		if(!prmClss[7].equals(String.class)) return false;
		if(!prmClss[8].equals(boolean.class)) return false;
		return true;
	}

	// getMtdsAndFldsをまだ実行していない場合のupdatePluginResult
	public <T> void updatePluginResult(Object obj, Class<T> cls) {
		HashMap<String, Method> plgMtdsMap = new HashMap<>();
		HashMap<String, Method[]> gsMtdsMap = new HashMap<>();
		HashMap<String, Field> fldMap = new HashMap<>();
		getPluginMethods(cls, plgMtdsMap);
		getMethodsAndFields(cls, gsMtdsMap, fldMap, gsMtdsMapMap, fldMapMap);

		updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
	}

	@SuppressWarnings("unchecked")
	private <T> void updatePluginResult(Object obj, Class<T> cls,
			Map<String, Method> plgMtdsMap, Map<String, Method[]> gsMtdsMap, Map<String, Field> fldMap) {
		if(gsMtdsMap == null || fldMap == null) {
			gsMtdsMap = new HashMap<>();
			fldMap = new HashMap<>();
			getMethodsAndFields(cls, gsMtdsMap, fldMap, gsMtdsMapMap, fldMapMap);
		}

		magicFloor.clear();
		currentFloor.clear();
		buildMagicFloorRecursive(gbd, rpd, obj, gsMtdsMap, fldMap, gsMtdsMapMap, fldMapMap,
				"", currentFloor, rb, 0, magicListDepthLimit, 0);

		// プラグイン自身の置換
		String key = My.constantize(Constants.MINION_SUFFIX);
		ReferenceStructure ref = new ReferenceStructure((T)obj, Constants.MINION_SUFFIX, cls);
		currentFloor.put(key, ref);

		// プラグインのルートのフィールド名でnullのとき一括で置換できるようにする
		for(Map.Entry<String, Method[]> ent : gsMtdsMap.entrySet()) {
			Method[] gsMtds = gsMtdsMap.get(ent.getKey());
			if(gsMtds[0] != null && gsMtds[1] != null) {
				Method gm = gsMtds[0];
				String kpc = gm.getName().substring(3);
				String kc = My.constantize(kpc);
				currentFloor.put(kc + "[", null);
			}
		}
		for(Map.Entry<String, Field> ent : fldMap.entrySet()) {
			if(gsMtdsMap.containsKey(ent.getKey())) {
				continue;
			}
			Field fld = fldMap.get(ent.getKey());
			String kcm = fld.getName();
			String kc = My.constantize(kcm);
			currentFloor.put(kc + "[", null);
		}

		copyToMagicFloor();

		magicPtrn = updateMagicPtrn();
		ptrnTower.removeLast();
		ptrnTower.add(magicPtrn);

		if(isLogMagicWordPattern) {
			if(magicPtrn == null) {
				logger.debug("magicPtrn: (null)");
			} else {
				logger.debug("magicPtrn: " + magicPtrn.pattern());
			}
		}
		if(isLogMagicWordValues) {
			logger.debug("magicWord: updated.");
			logMagicWordValues();
		}
	}

	// magicFloorに規定の階数分コピー
	private void copyToMagicFloor() {
		magicFloor.clear();

		int tmpLmt = magicTower.size() - magicFloorsThrough - 1;
		if(tmpLmt < 1) {
			tmpLmt = 1;
		}

		magicFloor.putAll(magicTower.get(0));

		for(int tmpIdx = tmpLmt; tmpIdx < magicTower.size(); tmpIdx++) {
			HashMap<String, ReferenceStructure> tmpFloor = magicTower.get(tmpIdx);
			magicFloor.putAll(tmpFloor);
		}

		// System.out.println("depth:" + magicTower.size() + " start:" + tmpLmt + " size:" + magicFloor.size());
	}

	public boolean isRedirectable(Object obj) {
		if(obj == null) return false;

		if(obj instanceof Class) {
			// obj = StringUtils.uncapitalize(((Class<?>)obj).getSimpleName()) + "." + skin.getExt();
			// プラグインパスが複数になったので廃止したい
			return false;
		}

		if(obj instanceof String) {
			// String ap = (String)obj;
			// String act = null;
			// String ext = null;
			// Matcher ap1mt = ap1pt.matcher(ap);
			// if(ap1mt.matches()) {
			// 	act = ap1mt.group(1);
			// 	ext = ap1mt.group(2);
			// } else {
			// 	Matcher ap2mt = ap2pt.matcher(ap);
			// 	if(ap2mt.matches()) {
			// 		act = ap2mt.group(1);
			// 		ext = ap2mt.group(2);
			// 	} else {
			// 		Matcher ap3mt = ap3pt.matcher(ap);
			// 		if(ap3mt.matches()) {
			// 			act = ap3mt.group(1);
			// 			ext = ap3mt.group(2);
			// 		} else {
			// 			Matcher ap4mt = ap4pt.matcher(ap);
			// 			if(ap4mt.matches()) {
			// 				act = ap4mt.group(1);
			// 				ext = ap4mt.group(2);
			// 			}
			// 		}
			// 	}
			// }

			// if(StringUtils.isNotEmpty(act) && StringUtils.isNotEmpty(ext)) {
			// 	// if(skin.hasSkin(null, act) || pm.hasPlugin(act)) {
			// 		return true;
			// 	// }
			// }

			return true;
		} else if(obj instanceof URI) {
			return true;
		}

		logger.warn("can't redirect to " + obj.toString());
		return false;
	}
/*
	public <T> Cache appendParsedTemplate(Object obj, Class<T> cls, Cache parent,
			String sKey, String dKey, String indent, boolean isForced, Time14 expires)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		updatePluginResult(obj, cls);
		return appendParsedTemplate(parent, sKey, dKey, indent, isForced, expires);
	}

	public <T> Cache appendParsedTemplate(Cache parent,
			String sKey, String dKey, String indent, boolean isForced, Time14 expires)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		Cache child = cacheParsedTemplate(sKey, dKey, indent, false, isForced, expires);
		Iterator<String> itr = child.getLines();
		while(itr.hasNext()) {
			String line = itr.next();
			if(line.length() > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(indent);
				sb.append(line);
				parent.addLine(sb.toString());
			}
		}

		return parent;
	}
*/
	public Cache appendParsedTemplate(Cache parent,
			String sKey, String args, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		return appendParsedTemplate(null, null, parent, sKey, args, indent, isForced);
	}

	public Cache appendParsedTemplate(Object obj, Class<?> cls, Cache parent,
			String sKey, String args, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		logger.trace("appendParsedTemplate start: " + sKey);

		if(obj != null && cls != null) {
			updatePluginResult(obj, cls);
		}

		String dKey = "";
		if(StringUtils.isNotEmpty(args)) {
			dKey = My.hs(args);
		}

		Cache cache = new Cache(sKey, dKey, skin.getExt(), new Time14());
		parseTemplate(cache, indent, "#", sKey, args, defaultBr, isForced);

		cache.copyRefFiles(parent);
		cache.copyMissFiles(parent);
		cache.copyChildKeys(parent);

		Iterator<String> itr = cache.getLines();
		while(itr.hasNext()) {
			String line = itr.next();
			if(line.length() > 0) {
				StringBuilder sb = new StringBuilder();
				// sb.append(indent);
				sb.append(line);
				parent.addLine(sb.toString());
			}
		}

		// addDebugCacheTree(cache, indent, "created");

		logger.trace("appendParsedTemplate end: " + sKey);
		return parent;
	}

	public Cache cacheParsedTemplate(String sKey, String dKey, String indent,
			boolean skipLoad, boolean isForced, Time14 expires)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		logger.trace("cacheParsedTemplate start: " + sKey);
		Cache cache = null;
		if(skipLoad || !isForced) {
			cache = gbd.loadCacheIfPossible(sKey, dKey, skin.getExt(), true);
			if(cache != null) {
				addDebugCacheTree(cache, indent, "loaded");
				logger.trace("cacheParsedTemplate end: " + sKey);
				return cache;
			}
		}

		cache = new Cache(sKey, dKey, skin.getExt(), new Time14(), expires == null ? Time14.FUTURE : expires);

		// parse template
		JavaFGets jfg = null;
		InputStream is = null;
		File skinFile = skin.getSkinFile(act, sKey, cache);
		if(skinFile == null) {
			logger.warn("no template " + sKey + ".");
			cache.addLine("(no template.)");
			addDebugCacheTree(cache, indent, "no template");
			logger.trace("cacheParsedTemplate end: " + sKey);
			return cache;
		}

		boolean ctRaised = false;
		try {
			raiseCacheTower(cache);
			ctRaised = true;

			is = new FileInputStream(skinFile);
			jfg = new JavaFGets(is);
			String line = null;
			Matcher tpmt1 = null;
			Matcher tpmt2 = null;
			boolean isCont = false;
			boolean isMatched = false;
			Matcher opnmt = null;
			Matcher clsmt = null;
			String opntg = null;
			String clstg = null;
			String indent2 = null;
			String type2 = null;
			String sKey2 = null;
			String args2 = null;
			String br2 = null;
			char sch = '\0';
			char ech = '\0';
			while((line = jfg.readLine()) != null) {
				line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
				// line = new String(line.getBytes("UTF-8"));

				if(isCont) {
					// 直前の行に連結する処理
					logger.trace("cacheParsedTemplate tppt1 start: " + sKey);
					tpmt1 = tppt1.matcher(line);
					if(tpmt1.matches()) {
						String tgt1 = tpmt1.group(2);
						int tgtLen = tgt1.length();
						String tgt2;
						if(tgtLen >= 2) {
							ech = tgt1.charAt(tgtLen - 1);
							if(ech == ',') {
								// さらに次の行に続く
								args2 += tgt1;
								continue;
							} else {
								isCont = false;
								tgt2 = tgt1;
							}

							logger.trace("cacheParsedTemplate clspt start: " + sKey);
							clsmt = clspt.matcher(tgt1);
							if(clsmt.find()) {
								clstg = clsmt.group(1);
								// 空を許すので絶対にマッチする
								tgtLen -= clstg.length();
								tgt2 = tgt1.substring(0, tgtLen);
							}
							logger.trace("cacheParsedTemplate clspt end: " + sKey);

							if(tgtLen > 1) {
								ech = tgt1.charAt(tgtLen - 1);
								if((sch == '(' && ech == ')')
										|| (sch == '{' && ech == '}')
										|| (sch == '[' && ech == ']')) {
									// 連結完了
									args2 += tgt2;
									isMatched = true;
								}
							}
						}
					}
					logger.trace("cacheParsedTemplate tppt1 end: " + sKey);

					if(!isMatched) {
						logger.warn("args is not continuous. sKey=" + sKey + ","
								+ "args=" + args2 + ", next=" + line);
						isCont = false;
						continue;
					}
				} else {
					isMatched = false;
					opntg = null;
					clstg = null;
					indent2 = null;
					type2 = null;
					sKey2 = null;
					args2 = null;
					br2 = null;
					sch = '\0';
					ech = '\0';

					// テンプレート/プラグイン展開
					logger.trace("cacheParsedTemplate tppt1 start: " + sKey);
					tpmt1 = tppt1.matcher(line);
					if(tpmt1.matches()) {
						indent2 = tpmt1.group(1);
						String tgt1 = tpmt1.group(2);
						br2 = tpmt1.group(3);

						String tgt2 = tgt1;
						logger.trace("cacheParsedTemplate opnpt start: " + sKey);
						opnmt = opnpt.matcher(tgt1);
						if(opnmt.lookingAt()) {
							opntg = opnmt.group(1);
							clsmt = clspt.matcher(tgt1);
							if(clsmt.find()) {
								clstg = clsmt.group(1);
								// 空を許すので絶対にマッチする
								tgt2 = tgt1.substring(opntg.length(),
										tgt1.length() - clstg.length());
							}
						}
						logger.trace("cacheParsedTemplate opnpt end: " + sKey);

						logger.trace("cacheParsedTemplate tppt2 start: " + sKey);
						tpmt2 = tppt2.matcher(tgt2);
						if(tpmt2.matches()) {
							String args = tpmt2.group(3);
							if(args.length() == 0) {
								isMatched = true;
								type2 = tpmt2.group(1);
								sKey2 = tpmt2.group(2);
								args2 = args;
							} else if(args.length() > 2) {
								sch = args.charAt(0);
								ech = args.charAt(args.length() - 1);
								if((sch == '(' && ech == ')')
										|| (sch == '{' && ech == '}')
										|| (sch == '[' && ech == ']')) {
									// 1行で終わる場合
									isMatched = true;
									type2 = tpmt2.group(1);
									sKey2 = tpmt2.group(2);
									args2 = args;
								} else if((sch == '(' || sch == '{' || sch == '[') && ech == ',') {
									// 次の行に続く場合
									type2 = tpmt2.group(1);
									sKey2 = tpmt2.group(2);
									args2 = args;
									isCont = true;
									continue;
								}
							}
						}
						logger.trace("cacheParsedTemplate tppt2 end: " + sKey);
					}
				}
				logger.trace("cacheParsedTemplate tppt1 end: " + sKey);

				if(isMatched) {
					if(args2.length() > 0 && magicPtrn != null) {
						boolean isJsonArg = args2.startsWith("{") || args2.startsWith("[");
						List<Integer[]> strRngs = new ArrayList<>(); // JSONエスケープ用置換の範囲
						if(isJsonArg) {
							Matcher mc1 = Pattern.compile("\"\"[A-Z0-9_]+\"\"").matcher(args2);
							while(mc1.find()) {
								int start = mc1.start();
								int end = start + mc1.group(0).length();
								strRngs.add(new Integer[] { start, end });
							}
						}

						// magic word展開
						logger.trace("cacheParsedTemplate magicPtrn start: " + sKey);
						Matcher mc2 = magicPtrn.matcher(args2);
						StringBuffer sb2 = new StringBuffer();
						boolean allMinion = false;
						while(mc2.find()) {
							if(isJsonArg) {
								boolean inRng = false;
								for(Integer[] strRng : strRngs) {
									int start = mc2.start();
									int end = start + mc2.group(0).length();
									if((strRng[0] <= start && start < strRng[1]) || (strRng[0] < end && end <= strRng[1])) {
										mc2.appendReplacement(sb2, Matcher.quoteReplacement(mc2.group(0)));
										inRng = true;
										break;
									}
								}
								if(inRng) continue;
							}

							String mwk = mc2.group(1);
							if(!isJsonArg && mwk.length() == args2.length() - 6
									&& mwk.endsWith(My.constantize(Constants.MINION_SUFFIX))) {
								// ()の中が全部minionの場合、{}で渡されたものとする
								allMinion = true;
							}
							ReferenceStructure ref = magicFloor.get(mwk);
							if(mwk.equals(My.constantize(Constants.MINION_SUFFIX))
									|| ReferenceConverter.convKey2Suf(mwk).equals(Constants.MINION_SUFFIX)) {
								if(ref == null) {
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->(null)");
									}
									mc2.appendReplacement(sb2, "null");
								} else {
									String mwv = ref.getMnn();
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->" + mwv);
									}
									mc2.appendReplacement(sb2, Matcher.quoteReplacement(mwv));
								}
							} else {
								if(ref == null) {
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->(null)");
									}
									mc2.appendReplacement(sb2, "");
								} else {
									String mwv = ref.conv(String.class).getObj();
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->" + mwv);
									}
									mc2.appendReplacement(sb2, Matcher.quoteReplacement(mwv));
								}
							}
						}
						mc2.appendTail(sb2);
						logger.trace("cacheParsedTemplate magicPtrn end: " + sKey);

						String tmpArg2 = sb2.toString();
						args2 = tmpArg2;
						if(isJsonArg) {
							String tmpArg3 = tmpArg2.replaceAll("\"\"([A-Z0-9_]+)\"\"", "__$1__");
							if(!tmpArg2.equals(tmpArg3)) {
								// JSONエスケープ用
								Matcher mc3 = magicPtrn.matcher(tmpArg3);
								StringBuffer sb3 = new StringBuffer();
								while(mc3.find()) {
									String mwk = mc3.group(1);
									ReferenceStructure ref = magicFloor.get(mwk);
									if(mwk.equals(My.constantize(Constants.MINION_SUFFIX))
											|| ReferenceConverter.convKey2Suf(mwk).equals(Constants.MINION_SUFFIX)) {
										if(ref == null) {
											if(isLogMagicWordReplace) {
												logger.debug("replace: " + mwk + "->(null)");
											}
											// mc3.appendReplacement(sb3, "\"\"");
											mc3.appendReplacement(sb3, "null");
										} else {
											String mwv = ref.getMnn();
											String mwv2 = SimpleJsonBuilder.escapeJson(mwv);
											String mwv3 = "\"" + mwv2 + "\"";
											if(isLogMagicWordReplace) {
												logger.debug("replace: " + mwk + "->" + mwv3);
											}
											mc3.appendReplacement(sb3, Matcher.quoteReplacement(mwv3));
										}
									} else {
										if(ref == null) {
											if(isLogMagicWordReplace) {
												logger.debug("replace: " + mwk + "->(null)");
											}
											// mc3.appendReplacement(sb3, "\"\"");
											mc3.appendReplacement(sb3, "null");
										} else {
											String mwv = ref.conv(String.class).getObj();
											String mwv2 = SimpleJsonBuilder.escapeJson(mwv);
											String mwv3 = "\"" + mwv2 + "\"";
											if(isLogMagicWordReplace) {
												logger.debug("replace: " + mwk + "->" + mwv3);
											}
											mc3.appendReplacement(sb3, Matcher.quoteReplacement(mwv3));
										}
									}
								}
								mc3.appendTail(sb3);

								args2 = sb3.toString();
							}
						}

						if(allMinion) {
							args2 = args2.substring(1, args2.length() - 1);
						}
					}

					if(cache.isSkipAfter()) {
						break;
					}

					parseTemplate(cache, indent2, type2, sKey2, args2, br2, isForced);

					if(cache.isSkipAfter()) {
						break;
					}
					continue;
				} else {
					if(magicPtrn != null) {
						// magic word展開
						logger.trace("cacheParsedTemplate magicPtrn start: " + sKey);
						Matcher mc = magicPtrn.matcher(line);
						StringBuffer sb = new StringBuffer();
						while(mc.find()) {
							String mwk = mc.group(1);
							ReferenceStructure ref = magicFloor.get(mwk);
							if(mwk.equals(My.constantize(Constants.MINION_SUFFIX))
									|| ReferenceConverter.convKey2Suf(mwk).equals(Constants.MINION_SUFFIX)) {
								if(ref == null) {
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->(null)");
									}
									mc.appendReplacement(sb, "null");
								} else {
									String mwv = ref.getMnn();
									String mwv2 = StringEscapeUtils.escapeHtml4(mwv);
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->" + mwv2);
									}
									mc.appendReplacement(sb, Matcher.quoteReplacement(mwv2));
								}
							} else {
								if(ref == null) {
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->(null)");
									}
									mc.appendReplacement(sb, "");
								} else {
									String mwv = ref.conv(String.class).getObj();
									String mwv2 = StringEscapeUtils.escapeHtml4(mwv);
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->" + mwv2);
									}
									mc.appendReplacement(sb, Matcher.quoteReplacement(mwv2));
								}
							}
						}
						mc.appendTail(sb);
						logger.trace("cacheParsedTemplate magicPtrn end: " + sKey);
						line = sb.toString();
					}
				}

				cache.addLine(line);
			}
		} catch(IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
		} finally {
			if(ctRaised) reduceCacheTower();

			try {
				if(is != null) {
					is.close();
				}
				if(jfg != null) {
					jfg.close();
				}
			} catch(Exception ex) {}
		}

		addDebugCacheTree(cache, indent, "created");

		logger.trace("cacheParsedTemplate end: " + sKey);
		return cache;
	}

	private void raiseMagicTower() {
		logger.trace("raiseMagicTower start.");
		// コンストラクタで最下層が作られる
		currentFloor = new HashMap<>();

		StringBuilder sb = new StringBuilder();
		boolean isRootCache = true;
		for(Cache tc : cacheTower) {
			if(isRootCache) {
				isRootCache = false;
				continue;
			}
			sb.append(tc.getSKey());
			sb.append(".");
		}
		ct = sb.toString();
		String ctKey = Constants.CACHE_TOWER_KEY;
		currentFloor.put(My.constantize(ctKey), new ReferenceStructure(ct));

		magicTower.add(currentFloor);
		copyToMagicFloor();

		logger.trace("raiseMagicTower updateMagicPtrn start.");
		magicPtrn = updateMagicPtrn();
		logger.trace("raiseMagicTower updateMagicPtrn end.");
		ptrnTower.add(magicPtrn);

		if(isLogMagicTower) {
			logger.info("magicTower raised: " + ct);
		}
		if(isLogMagicWordPattern) {
			if(magicPtrn == null) {
				logger.debug("magicPtrn: (null)");
			} else {
				logger.debug("magicPtrn: " + magicPtrn.pattern());
			}
		}
		if(isLogMagicWordValues) {
			logger.debug("magicWord: raised.");
			logMagicWordValues();
		}
		logger.trace("raiseMagicTower end.");
	}

	public static void buildMagicFloorRecursive(GlobalData gbd, ResponseData rpd,
			Object obj, Map<String, Method[]> gsMtdsMap, Map<String, Field> fldMap,
			Map<Class<?>, Map<String, Method[]>> gsMtdsMapMap,
			Map<Class<?>, Map<String, Field>> fldMapMap, String ns,
			HashMap<String, ReferenceStructure> currentFloor, ResourceBundle rb,
			int listDepth, int listLimit, int magicDepth) {
		buildMagicFloorRecursive(gbd, rpd, obj, null, null, gsMtdsMap, fldMap, gsMtdsMapMap, fldMapMap,
				ns, currentFloor, rb, listDepth, listLimit, magicDepth);
	}

	public static void buildMagicFloorRecursive(GlobalData gbd, ResponseData rpd,
			Object obj, Class<?> genType, ParamSidePack genPsp,
			Map<String, Method[]> gsMtdsMap, Map<String, Field> fldMap,
			Map<Class<?>, Map<String, Method[]>> gsMtdsMapMap,
			Map<Class<?>, Map<String, Field>> fldMapMap, String ns,
			HashMap<String, ReferenceStructure> currentFloor, ResourceBundle rb,
			int listDepth, int listLimit, int magicDepth) {
		if(magicDepth > MAGIC_FLOORS_LIMIT) {
			logger.warn("depth exceeded.");
			return;
		}

		if(obj instanceof FileItem
				|| (genType != null && FileItem.class.isAssignableFrom(genType))) {
			return;
		} else if(obj instanceof List) {
			if(genType != null && BracketsNode.class.isAssignableFrom(genType)) {
				// HtmlElement等のnodesは数が増えやすいので省略
				return;
			} else if(listDepth == listLimit) {
				return;
			}
			listDepth++;

			List<?> list = (List<?>)obj;
			Class<?> cls = genType;
			String suf = null;
			Class<? extends Converter> genCnvCls = null;
			if(genPsp != null) {
				genCnvCls = genPsp.getConversionClass();
			}
			for(int idx = 0; idx < list.size(); idx++) {
				int no = idx + 1;
				Object val = list.get(idx);

				String[] kecs;
				if(idx == list.size() - 1 && StringUtils.isNotEmpty(Constants.LIST_LAST_SUFFIX)) {
					kecs = new String[] { ns + no, ns + Constants.LIST_LAST_SUFFIX };
				} else {
					kecs = new String[] { ns + no };
				}
				ParamSidePack psp = ParamSidePack.byParent(genPsp, kecs[0], rpd);
				for(String kec : kecs) {
					ReferenceStructure ref = null;
					boolean hasCnvErr = false;
					if(val == null && ParamSidePack.hasConversionReference(psp)) {
						String kc = My.constantize(kec);
						ref = psp.getConversionReference();
						currentFloor.put(kc, ref);
						if(isLogAllMagicWords) {
							logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
						}
						hasCnvErr = true;
					} else if(!ParamSidePack.hasValidationMessage(psp)
							|| !ParamSidePack.hasConversionReference(psp)) {
						String kc;
						if(val == null) {
						} else {
							if(cls == null) {
								cls = val.getClass();
								if(FileItem.class.isAssignableFrom(cls)) {
									return;
								}
							}
							if(suf == null) {
								suf = ReferenceConverter.convCls2Suf(cls);
							}

							boolean found = false;
							if(StringUtils.isEmpty(suf)) {
								kc = My.constantize(kec);
								ref = ReferenceConverter.genStructureAssortedType(val, null, cls, genCnvCls);
								found = true;
							} else if(suf.equals(Constants.MINION_SUFFIX)
									&& !isToMinion(suf, gbd, obj, ns, listDepth, listLimit, magicDepth)) {
								kc = null;
							} else {
								kc = My.constantize(kec + "." + suf);
								if(List.class.isAssignableFrom(cls)) {
									List<?> list2 = (List<?>)val;
									if(list2.size() == 0 || list2.get(0) == null) {
										ref = ReferenceConverter.genStructureAssortedType(list2, suf, String.class);
									} else {
										Object val2 = list2.get(0);
										ref = ReferenceConverter.genStructureAssortedType(list2, suf, val2.getClass(), genCnvCls);
									}
								} else {
									ref = ReferenceConverter.genStructureAssortedType(val, suf, cls);
								}
								found = true;
								buildMagicFloorRecursive(gbd, rpd, val, null, null, gsMtdsMapMap, fldMapMap,
										kec + ".", currentFloor, rb, listDepth, listLimit, magicDepth + 1);
							}
							if(found) {
								currentFloor.put(kc, ref);
								if(isLogAllMagicWords) {
									logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
								}
							}
						}
					} else {
						String kc = My.constantize(kec);
						ref = psp.getConversionReference();
						currentFloor.put(kc, ref);
						if(isLogAllMagicWords) {
							logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
						}
					}
					if(hasCnvErr) {
						String cnvErrMsg = rb.getString("CONVERSION_ERROR_MESSAGE");
						ReferenceStructure refVld = ReferenceConverter.genStructureAssortedType(cnvErrMsg,
								null, String.class);
						String kcVld = My.constantize(kec + Constants.VLD_MSG_KEY_SUFFIX);
						currentFloor.put(kcVld, refVld);
						if(isLogAllMagicWords) {
							logger.debug("magic word: \"" + kcVld + "\"=" + refVld.getMnn());
						}
					} else if(ParamSidePack.hasValidationMessage(psp)) {
						ReferenceStructure refVld = ReferenceConverter.genStructureAssortedType(psp.getValidationMessage(),
								null, String.class);
						String kcVld = My.constantize(kec + Constants.VLD_MSG_KEY_SUFFIX);
						currentFloor.put(kcVld, refVld);
						if(isLogAllMagicWords) {
							logger.debug("magic word: \"" + kcVld + "\"=" + refVld.getMnn());
						}
					}
				}
			}
			if(StringUtils.isNotEmpty(Constants.LIST_NUMBER_SUFFIX)) {
				String kec = ns + Constants.LIST_NUMBER_SUFFIX;
				String kc = My.constantize(kec);
				currentFloor.put(kc, new ReferenceStructure("" + list.size()));
				if(isLogAllMagicWords) {
					logger.debug("magic word: \"" + kc + "\"=" + list.size());
				}
			}
		} else if(obj instanceof Map) {
			Map<?, ?> map = (Map<?, ?>)obj;
			for(Map.Entry<?, ?> ent : map.entrySet()) {
				String key = ent.getKey().toString();
				Object val = ent.getValue();

				String kec = ns + key;
				String kc;
				if(val == null) {
				} else {
					Class<?> cls = val.getClass();
					if(FileItem.class.isAssignableFrom(cls)) {
						return;
					}
					String suf = ReferenceConverter.convCls2Suf(cls);
					ReferenceStructure ref;
					boolean found = false;
					if(StringUtils.isEmpty(suf)) {
						kc = My.constantize(kec);
						ref = ReferenceConverter.genStructureAssortedType(val, null, cls);
						found = true;
					} else if(suf.equals(Constants.MINION_SUFFIX)
							&& !isToMinion(suf, gbd, obj, ns, listDepth, listLimit, magicDepth)) {
						kc = null;
						ref = null;
					} else {
						kc = My.constantize(kec + "." + suf);
						if(List.class.isAssignableFrom(cls)) {
							List<?> list2 = (List<?>)val;
							if(list2.size() == 0 || list2.get(0) == null) {
								ref = ReferenceConverter.genStructureAssortedType(list2, suf, String.class);
							} else {
								Object val2 = list2.get(0);
								ref = ReferenceConverter.genStructureAssortedType(list2, suf, val2.getClass());
							}
						} else {
							ref = ReferenceConverter.genStructureAssortedType(val, suf, cls);
						}
						found = true;
						buildMagicFloorRecursive(gbd, rpd, val, null, null, gsMtdsMapMap, fldMapMap,
								kec + ".", currentFloor, rb, listDepth, listLimit, magicDepth + 1);
					}
					if(found) {
						currentFloor.put(kc, ref);
						if(isLogAllMagicWords) {
							logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
						}
					}
				}
			}
			if(StringUtils.isNotEmpty(Constants.MAP_NUMBER_SUFFIX)) {
				String kec = ns + Constants.MAP_NUMBER_SUFFIX;
				String kc = My.constantize(kec);
				currentFloor.put(kc, new ReferenceStructure("" + map.size()));
				if(isLogAllMagicWords) {
					logger.debug("magic word: \"" + kc + "\"=" + map.size());
				}
			}
		} else {
			if(gsMtdsMap == null || fldMap == null) {
				gsMtdsMap = new HashMap<>();
				fldMap = new HashMap<>();
				getMethodsAndFields(obj.getClass(), gsMtdsMap, fldMap, gsMtdsMapMap, fldMapMap);
			}
			if(gsMtdsMap.size() == 0 && fldMap.size() == 0) {
				return;
			}

			try {
				for(Map.Entry<String, Method[]> ent : gsMtdsMap.entrySet()) {
					// メソッド
					Method[] gsMtds = gsMtdsMap.get(ent.getKey());
					if(gsMtds[0] != null && gsMtds[1] != null) {
						Method gm = gsMtds[0];
						Field fld = fldMap.get(ent.getKey());
						if(gm.isAnnotationPresent(OutOfResponseData.class)) {
							continue;
						} else if(fld != null) {
							if(fld.isAnnotationPresent(OutOfResponseData.class)) {
								continue;
							}
						}

						String kpc = ns + StringUtils.uncapitalize(gm.getName().substring(3));
						ParamSidePack psp = ParamSidePack.byConversionClass(gsMtds, fld, kpc, rpd);

						Object val = gm.invoke(obj);
						boolean hasCnvErr = false;
						if(val == null && ParamSidePack.hasConversionReference(psp)) {
							String kc = My.constantize(kpc);
							ReferenceStructure ref = psp.getConversionReference();
							currentFloor.put(kc, ref);
							if(isLogAllMagicWords) {
								logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
							}
							hasCnvErr = true;
						} else if(!ParamSidePack.hasValidationMessage(psp)
								|| !ParamSidePack.hasConversionReference(psp)) {
							Class<?> cls = gm.getReturnType();
							if(FileItem.class.isAssignableFrom(cls)) {
								continue;
							}
							Class<?> aClass = null;
							if(List.class.isAssignableFrom(cls)) {
								Type type = gm.getGenericReturnType();
								ParameterizedType gType = (ParameterizedType)type;
								Type[] aTypes = gType.getActualTypeArguments();
								aClass = (Class<?>)aTypes[0];
								if(FileItem.class.isAssignableFrom(aClass)) {
									continue;
								}
							}

							String kc;
							if(val == null) {
							} else {
								String suf = ReferenceConverter.convCls2Suf(cls);
								ReferenceStructure ref;
								Class<? extends Converter> cnvCls = null;
								if(psp != null) {
									cnvCls = psp.getConversionClass();
								}

								boolean found = false;
								if(StringUtils.isEmpty(suf)) {
									kc = My.constantize(kpc);
									ref = ReferenceConverter.genStructureAssortedType(val, null, cls, cnvCls);
									found = true;
								} else if(suf.equals(Constants.MINION_SUFFIX)
										&& !isToMinion(suf, gbd, obj, ns, listDepth, listLimit, magicDepth)) {
									kc = null;
									ref = null;
								} else {
									kc = My.constantize(kpc + "." + suf);
									if(List.class.isAssignableFrom(cls)) {
										List<?> list2 = (List<?>)val;
										if(list2.size() == 0 || list2.get(0) == null) {
											ref = ReferenceConverter.genStructureAssortedType(list2, suf, String.class);
										} else {
											Object val2 = list2.get(0);
											ref = ReferenceConverter.genStructureAssortedType(list2, suf, val2.getClass(), cnvCls);
										}
										found = true;
										buildMagicFloorRecursive(gbd, rpd, val, aClass, psp, null, null,
												gsMtdsMapMap, fldMapMap, kpc + ".", currentFloor, rb,
												listDepth, listLimit, magicDepth + 1);
									} else {
										ref = ReferenceConverter.genStructureAssortedType(val, suf, cls);
										found = true;
										buildMagicFloorRecursive(gbd, rpd, val, null, null, gsMtdsMapMap, fldMapMap,
												kpc + ".", currentFloor, rb, listDepth, listLimit, magicDepth + 1);
									}
								}
								if(found) {
									currentFloor.put(kc, ref);
									if(isLogAllMagicWords) {
										logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
									}
								}
							}
						} else {
							String kc = My.constantize(kpc);
							ReferenceStructure ref = psp.getConversionReference();
							currentFloor.put(kc, ref);
							if(isLogAllMagicWords) {
								logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
							}
						}
						if(hasCnvErr) {
							String cnvErrMsg = rb.getString("CONVERSION_ERROR_MESSAGE");
							ReferenceStructure refVld = ReferenceConverter.genStructureAssortedType(cnvErrMsg,
									null, String.class);
							String kcVld = My.constantize(kpc + Constants.VLD_MSG_KEY_SUFFIX);
							currentFloor.put(kcVld, refVld);
							if(isLogAllMagicWords) {
								logger.debug("magic word: \"" + kcVld + "\"=" + refVld.getMnn());
							}
						} else if(ParamSidePack.hasValidationMessage(psp)) {
							ReferenceStructure refVld = ReferenceConverter.genStructureAssortedType(psp.getValidationMessage(),
									null, String.class);
							String kcVld = My.constantize(kpc + Constants.VLD_MSG_KEY_SUFFIX);
							currentFloor.put(kcVld, refVld);
							if(isLogAllMagicWords) {
								logger.debug("magic word: \"" + kcVld + "\"=" + refVld.getMnn());
							}
						}
					}
				}
				for(Map.Entry<String, Field> ent : fldMap.entrySet()) {
					// フィールド
					if(gsMtdsMap.containsKey(ent.getKey())) {
						continue;
					}
					Field fld = fldMap.get(ent.getKey());
					if(fld.isAnnotationPresent(OutOfResponseData.class)) {
						continue;
					}

					String kcm = ns + fld.getName();
					ParamSidePack psp = ParamSidePack.byConversionClass(fld, kcm, rpd);

					Object val = fld.get(obj);
					boolean hasCnvErr = false;
					if(val == null && ParamSidePack.hasConversionReference(psp)) {
						String kc = My.constantize(kcm);
						ReferenceStructure ref = psp.getConversionReference();
						currentFloor.put(kc, ref);
						if(isLogAllMagicWords) {
							logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
						}
						hasCnvErr = true;
					} else if(!ParamSidePack.hasValidationMessage(psp)
							|| !ParamSidePack.hasConversionReference(psp)) {
						Class<?> cls = fld.getType();
						if(FileItem.class.isAssignableFrom(cls)) {
							continue;
						}
						Class<?> aClass = null;
						if(List.class.isAssignableFrom(cls)) {
							ParameterizedType gType = (ParameterizedType)fld.getGenericType();
							Type[] aTypes = gType.getActualTypeArguments();
							aClass = (Class<?>)aTypes[0];
							if(FileItem.class.isAssignableFrom(aClass)) {
								continue;
							}
						}

						String kc;
						if(val == null) {
						} else {
							String suf = ReferenceConverter.convCls2Suf(cls);
							ReferenceStructure ref;
							Class<? extends Converter> cnvCls = null;
							if(psp != null) {
								cnvCls = psp.getConversionClass();
							}

							boolean found = false;
							if(StringUtils.isEmpty(suf)) {
								kc = My.constantize(kcm);
								ref = ReferenceConverter.genStructureAssortedType(val, null, cls, cnvCls);
								found = true;
							} else if(suf.equals(Constants.MINION_SUFFIX)
									&& !isToMinion(suf, gbd, obj, ns, listDepth, listLimit, magicDepth)) {
								kc = null;
								ref = null;
							} else {
								kc = My.constantize(kcm + "." + suf);
								if(List.class.isAssignableFrom(cls)) {
									List<?> list2 = (List<?>)val;
									if(list2.size() == 0 || list2.get(0) == null) {
										ref = ReferenceConverter.genStructureAssortedType(list2, suf, String.class);
									} else {
										Object val2 = list2.get(0);
										ref = ReferenceConverter.genStructureAssortedType(list2, suf, val2.getClass(), cnvCls);
									}
									found = true;
									buildMagicFloorRecursive(gbd, rpd, val, aClass, psp, null, null,
											gsMtdsMapMap, fldMapMap, kcm + ".", currentFloor, rb,
											listDepth, listLimit, magicDepth + 1);
								} else {
									ref = ReferenceConverter.genStructureAssortedType(val, suf, cls);
									found = true;
									buildMagicFloorRecursive(gbd, rpd, val, null, null, gsMtdsMapMap, fldMapMap,
											kcm + ".", currentFloor, rb, listDepth, listLimit, magicDepth + 1);
								}
							}
							if(found) {
								currentFloor.put(kc, ref);
								if(isLogAllMagicWords) {
									logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
								}
							}
						}
					} else {
						String kc = My.constantize(kcm);
						ReferenceStructure ref = psp.getConversionReference();
						currentFloor.put(kc, ref);
						if(isLogAllMagicWords) {
							logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
						}
					}
					if(hasCnvErr) {
						String cnvErrMsg = rb.getString("CONVERSION_ERROR_MESSAGE");
						ReferenceStructure refVld = ReferenceConverter.genStructureAssortedType(cnvErrMsg,
								null, String.class);
						String kcVld = My.constantize(kcm + Constants.VLD_MSG_KEY_SUFFIX);
						currentFloor.put(kcVld, refVld);
						if(isLogAllMagicWords) {
							logger.debug("magic word: \"" + kcVld + "\"=" + refVld.getMnn());
						}
					} else if(ParamSidePack.hasValidationMessage(psp)) {
						ReferenceStructure refVld = ReferenceConverter.genStructureAssortedType(psp.getValidationMessage(),
								null, String.class);
						String kcVld = My.constantize(kcm + Constants.VLD_MSG_KEY_SUFFIX);
						currentFloor.put(kcVld, refVld);
						if(isLogAllMagicWords) {
							logger.debug("magic word: \"" + kcVld + "\"=" + refVld.getMnn());
						}
					}
				}
			} catch(IllegalAccessException iae) {
				logger.error(iae.getMessage(), iae);
			} catch(IllegalArgumentException iae) {
				logger.error(iae.getMessage(), iae);
			} catch(InvocationTargetException ite) {
				logger.error(ite.getMessage(), ite);
			}
		}
	}

	private static boolean isToMinion(String suf, GlobalData gbd, Object obj,
			String ns, int listDepth, int listLimit, int magicDepth) {
		if(magicDepth >= minionDepthLimit) {
			return false;
		} else if(isToMinionSchemaOrgProperty && obj instanceof SchemaOrgProperty) {
			return false;
		}

		return true;
	}

	private Pattern updateMagicPtrn() {
		if(magicFloor.size() == 0) {
			return null;
		}

		List<String> kl1 = new LinkedList<String>();
		for(String key : magicFloor.keySet()) {
			kl1.add(key);
		}
		kl1.sort((a, b) -> {
			ReferenceStructure av = magicFloor.get(a);
			ReferenceStructure bv = magicFloor.get(b);
			if(av != null && bv == null) {
				return -1;
			} else if(av == null && bv != null) {
				return 1;
			} else {
				return b.length() - a.length();
			}
		});
		List<String> kl2 = new LinkedList<String>();
		for(String key : kl1) {
			if(magicFloor.get(key) == null) {
				kl2.add(key + "A-Z0-9_]*");
			} else {
				kl2.add(key);
			}
		}

		String magicExpr = StringUtils.join(kl2, "|");
		return Pattern.compile("__(" + magicExpr + ")__");
	}

	private void reduceMagicTower() {
		magicTower.removeLast();
		if(magicTower.size() > 0) {
			currentFloor = magicTower.getLast();
		} else {
			currentFloor = null;
		}

		String tmpCT = ct;
		StringBuilder sb = new StringBuilder();
		boolean isRootCache = true;
		for(Cache tc : cacheTower) {
			if(isRootCache) {
				isRootCache = false;
				continue;
			}
			sb.append(tc.getSKey());
			sb.append(".");
		}
		ct = sb.toString();
		String ctKey = Constants.CACHE_TOWER_KEY;
		if(currentFloor != null) {
			currentFloor.put(My.constantize(ctKey), new ReferenceStructure(ct));
		}

		copyToMagicFloor();

		ptrnTower.removeLast();
		if(ptrnTower.size() > 0) {
			magicPtrn = ptrnTower.getLast();
		} else {
			magicPtrn = null;
		}

		if(isLogMagicTower) {
			logger.info("magicTower reduced: " + tmpCT);
		}
		if(isLogMagicWordPattern) {
			if(magicPtrn == null) {
				logger.debug("magicPtrn: (null)");
			} else {
				logger.debug("magicPtrn: " + magicPtrn.pattern());
			}
		}
		if(isLogMagicWordValues) {
			logger.debug("magicWord: reduced.");
			logMagicWordValues();
		}
	}

	private void raiseCacheTower(Cache cache) {
		cacheTower.add(cache);
		submitTower.add(submitCalled);
	}

	private void reduceCacheTower() {
		cacheTower.removeLast();
		submitTower.removeLast();
		if(submitTower.size() == 0) {
			submitCalled = false;
		} else {
			submitCalled = submitTower.getLast();
		}
	}

	public String convMagicWord(String key) {
		if(magicFloor.containsKey(key)) {
			ReferenceStructure ref = magicFloor.get(key);
			if(ref == null) {
				return "";
			} else if(key.equals(My.constantize(Constants.MINION_SUFFIX))
					|| ReferenceConverter.convKey2Suf(key).equals(Constants.MINION_SUFFIX)) {
				return ref.getMnn();
			} else {
				return ref.conv(String.class).getObj();
			}
		} else {
			return "";
		}
	}

	// ToDo: ネストしたList
	@SuppressWarnings("unchecked")
	private <T> T createObjectAndCopyFlx(Class<T> clsDst, Object objSrc, Class<?> clsSrc, ParamSidePack psp, String ns)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		if(objSrc == null) {
			return null;
		} else if(ReferenceStructure.class.isAssignableFrom(clsSrc)) {
			ReferenceStructure ref = (ReferenceStructure)objSrc;
			return ref.conv(clsDst).getObj();
		} else if(List.class.isAssignableFrom(clsSrc)) {
			List<Object> lst = (List<Object>)objSrc;
			if(lst.size() == 0) {
				return null;
			} else {
				objSrc = lst.get(0);
				if(objSrc == null) {
					return null;
				}
				clsSrc = objSrc.getClass();
			}
		}

		if(clsDst.isAssignableFrom(clsSrc)) {
			return (T)objSrc;
		} else if((String.class.isAssignableFrom(clsSrc) || clsSrc.isPrimitive()
					|| Number.class.isAssignableFrom(clsSrc) || Boolean.class.isAssignableFrom(clsSrc)
					|| Date.class.isAssignableFrom(clsSrc) || Calendar.class.isAssignableFrom(clsSrc)
					|| OffsetDateTime.class.isAssignableFrom(clsSrc) || LocalDateTime.class.isAssignableFrom(clsSrc)
					|| LocalDate.class.isAssignableFrom(clsSrc) || LocalTime.class.isAssignableFrom(clsSrc))
				&& (String.class.isAssignableFrom(clsDst) || clsDst.isPrimitive()
					|| Number.class.isAssignableFrom(clsDst) || Boolean.class.isAssignableFrom(clsDst)
					|| Date.class.isAssignableFrom(clsDst) || Calendar.class.isAssignableFrom(clsDst)
					|| OffsetDateTime.class.isAssignableFrom(clsDst) || LocalDateTime.class.isAssignableFrom(clsDst)
					|| LocalDate.class.isAssignableFrom(clsDst) || LocalTime.class.isAssignableFrom(clsDst))) {
			try {
				return (T)ReferenceConverter.getConvertUtils().convert(objSrc, clsDst);
			} catch(ConversionException ce) {
				logger.info(ce.getMessage());
				return null;
			}
		}

		// System.out.println("clsSrc: " + clsSrc.toString());
		// System.out.println("clsDst: " + clsDst.toString());
		// T objDst = clsDst.newInstance();
		T objDst = My.deminion("{}", clsDst);
		copyObjectFlx(objDst, clsDst, objSrc, clsSrc, ns);
		return objDst;
	}

	// ToDo: copyMapFlxを作るべきでは

	private <T> void copyObjectFlx(T objDst, Class<T> clsDst, Object objSrc, Class<?> clsSrc, String ns)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Map<String, Method[]> gsMtdsDstMap = new HashMap<>();
		Map<String, Field> fldDstMap = new HashMap<>();
		getMethodsAndFields(clsDst, gsMtdsDstMap, fldDstMap, gsMtdsMapMap, fldMapMap);

		if(objSrc instanceof Map) {
			Map<?, ?> mapSrc = (Map<?, ?>)objSrc;
			for(Map.Entry<?, ?> ent : mapSrc.entrySet()) {
				String keySrc = ent.getKey().toString().toLowerCase();
				if(gsMtdsDstMap.containsKey(keySrc)) {
					Method[] gsMtdsDst = gsMtdsDstMap.get(keySrc);
					Method smDst = gsMtdsDst[1];
					if(gsMtdsDst[0] != null && gsMtdsDst[1] != null) {
						Class<?>[] prmClssDst = smDst.getParameterTypes();
						if(prmClssDst.length == 1) {
							// マップ→メソッドのコピーが可能

							Object valSrc = ent.getValue();
							String kpc = StringUtils.uncapitalize(smDst.getName().substring(3));
							if(valSrc != null) {
								Field fld = fldDstMap.get(ent.getKey());
								ParamSidePack psp = ParamSidePack.byConversionClass(gsMtdsDst, fld, ns + kpc, rpd);
								if(List.class.isAssignableFrom(prmClssDst[0])) {
									ParameterizedType gType = (ParameterizedType)smDst.getGenericParameterTypes()[0];
									Type[] aTypes = gType.getActualTypeArguments();
									if(!ParamSidePack.hasConversionClass(psp)) {
										List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass(), psp, ns + kpc + ".");
										smDst.invoke(objDst, lstDst);
									} else {
										List<String> lstDst1 = createListAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kpc + ".");
										ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), (Class<?>)aTypes[0]);
										List<Object> lstDst2 = new ArrayList<>();
										for(String str : lstDst1) {
											try {
												Object val = cub.convert(str, (Class<?>)aTypes[0]);
												lstDst2.add(val);
											} catch(ConversionException ce) {
												logger.info(ce.getMessage());
												lstDst2.add(null);
											}
										}
										smDst.invoke(objDst, lstDst2);
									}
								} else {
									if(!ParamSidePack.hasConversionClass(psp)) {
										Object valDst = createObjectAndCopyFlx(prmClssDst[0], valSrc, valSrc.getClass(), psp, ns + kpc + ".");
										smDst.invoke(objDst, valDst);
									} else {
										String valDst1 = createObjectAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kpc + ".");
										ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), prmClssDst[0]);
										try {
											Object valDst2 = cub.convert(valDst1, prmClssDst[0]);
											smDst.invoke(objDst, valDst2);
										} catch(ConversionException ce) {
											logger.info(ce.getMessage());
											smDst.invoke(objDst, new Object[] { null });
										}
									}
								}
							}
						}
					}
				} else if(fldDstMap.containsKey(keySrc)) {
					Field fldDst = fldDstMap.get(keySrc);
					// マップ→フィールドのコピーが可能

					Object valSrc = ent.getValue();
					String kcm = fldDst.getName();
					if(valSrc != null) {
						ParamSidePack psp = ParamSidePack.byConversionClass(fldDst, ns + kcm, rpd);
						if(List.class.isAssignableFrom(fldDst.getType())) {
							ParameterizedType gType = (ParameterizedType)fldDst.getGenericType();
							Type[] aTypes = gType.getActualTypeArguments();
							if(!ParamSidePack.hasConversionClass(psp)) {
								List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass(), psp, ns + kcm + ".");
								fldDst.set(objDst, lstDst);
							} else {
								List<String> lstDst1 = createListAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kcm + ".");
								ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), (Class<?>)aTypes[0]);
								List<Object> lstDst2 = new ArrayList<>();
								for(String str : lstDst1) {
									try {
										Object val = cub.convert(str, (Class<?>)aTypes[0]);
										lstDst2.add(val);
									} catch(ConversionException ce) {
										logger.info(ce.getMessage());
										lstDst2.add(null);
									}
								}
								fldDst.set(objDst, lstDst2);
							}
						} else {
							if(!ParamSidePack.hasConversionClass(psp)) {
								Object valDst = createObjectAndCopyFlx(fldDst.getType(), valSrc, valSrc.getClass(), psp, ns + kcm + ".");
								fldDst.set(objDst, valDst);
							} else {
								String valDst1 = createObjectAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kcm + ".");
								ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), fldDst.getType());
								try {
									Object valDst2 = cub.convert(valDst1, fldDst.getType());
									fldDst.set(objDst, valDst2);
								} catch(ConversionException ce) {
									logger.info(ce.getMessage());
									fldDst.set(objDst, null);
								}
							}
						}
					}
				}
			}
		} else {
			Map<String, Method[]> gsMtdsSrcMap = new HashMap<>();
			Map<String, Field> fldSrcMap = new HashMap<>();
			getMethodsAndFields(clsSrc, gsMtdsSrcMap, fldSrcMap, gsMtdsMapMap, fldMapMap);

			for(Map.Entry<String, Method[]> ent : gsMtdsSrcMap.entrySet()) {
				// メソッド
				Method[] gsMtdsSrc = gsMtdsSrcMap.get(ent.getKey());
				if(gsMtdsSrc[0] != null && gsMtdsSrc[1] != null) {
					Method smSrc = gsMtdsSrc[1];
					Class<?>[] prmClssSrc = smSrc.getParameterTypes();
					if(prmClssSrc.length == 1) {
						Method gmSrc = gsMtdsSrc[0];
						if(gsMtdsDstMap.containsKey(ent.getKey())) {
							Method[] gsMtdsDst = gsMtdsDstMap.get(ent.getKey());
							Method smDst = gsMtdsDst[1];
							if(gsMtdsDst[0] != null && gsMtdsDst[1] != null) {
								Class<?>[] prmClssDst = smDst.getParameterTypes();
								if(prmClssDst.length == 1) {
									// メソッド→メソッドのコピーが可能

									Object valSrc = gmSrc.invoke(objSrc);
									String kpc = StringUtils.uncapitalize(smDst.getName().substring(3));
									if(valSrc != null) {
										Field fld = fldDstMap.get(ent.getKey());
										ParamSidePack psp = ParamSidePack.byConversionClass(gsMtdsDst, fld, ns + kpc, rpd);
										if(List.class.isAssignableFrom(prmClssDst[0])) {
											ParameterizedType gType = (ParameterizedType)smDst.getGenericParameterTypes()[0];
											Type[] aTypes = gType.getActualTypeArguments();
											if(!ParamSidePack.hasConversionClass(psp)) {
												List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass(), psp, ns + kpc + ".");
												smDst.invoke(objDst, lstDst);
											} else {
												List<String> lstDst1 = createListAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kpc + ".");
												ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), (Class<?>)aTypes[0]);
												List<Object> lstDst2 = new ArrayList<>();
												for(String str : lstDst1) {
													try {
														Object val = cub.convert(str, (Class<?>)aTypes[0]);
														lstDst2.add(val);
													} catch(ConversionException ce) {
														logger.info(ce.getMessage());
														lstDst2.add(null);
													}
												}
												smDst.invoke(objDst, lstDst2);
											}
										} else {
											if(!ParamSidePack.hasConversionClass(psp)) {
												Object valDst = createObjectAndCopyFlx(prmClssDst[0], valSrc, valSrc.getClass(), psp, ns + kpc + ".");
												smDst.invoke(objDst, valDst);
											} else {
												String valDst1 = createObjectAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kpc + ".");
												ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), prmClssDst[0]);
												try {
													Object valDst2 = cub.convert(valDst1, prmClssDst[0]);
													smDst.invoke(objDst, valDst2);
												} catch(ConversionException ce) {
													logger.info(ce.getMessage());
													smDst.invoke(objDst, new Object[] { null });
												}
											}
										}
									}
								}
							}
						} else if(fldDstMap.containsKey(ent.getKey())) {
							Field fldDst = fldDstMap.get(ent.getKey());
							// メソッド→フィールドのコピーが可能

							Object valSrc = gmSrc.invoke(objSrc);
							String kcm = fldDst.getName();
							if(valSrc != null) {
								ParamSidePack psp = ParamSidePack.byConversionClass(fldDst, ns + kcm, rpd);
								if(List.class.isAssignableFrom(fldDst.getType())) {
									ParameterizedType gType = (ParameterizedType)fldDst.getGenericType();
									Type[] aTypes = gType.getActualTypeArguments();
									if(!ParamSidePack.hasConversionClass(psp)) {
										List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass(), psp, ns + kcm + ".");
										fldDst.set(objDst, lstDst);
									} else {
										List<String> lstDst1 = createListAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kcm + ".");
										ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), (Class<?>)aTypes[0]);
										List<Object> lstDst2 = new ArrayList<>();
										for(String str : lstDst1) {
											try {
												Object val = cub.convert(str, (Class<?>)aTypes[0]);
												lstDst2.add(val);
											} catch(ConversionException ce) {
												logger.info(ce.getMessage());
												lstDst2.add(null);
											}
										}
										fldDst.set(objDst, lstDst2);
									}
								} else {
									if(!ParamSidePack.hasConversionClass(psp)) {
										Object valDst = createObjectAndCopyFlx(fldDst.getType(), valSrc, valSrc.getClass(), psp, ns + kcm + ".");
										fldDst.set(objDst, valDst);
									} else {
										String valDst1 = createObjectAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kcm + ".");
										ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), fldDst.getType());
										try {
											Object valDst2 = cub.convert(valDst1, fldDst.getType());
											fldDst.set(objDst, valDst2);
										} catch(ConversionException ce) {
											logger.info(ce.getMessage());
											fldDst.set(objDst, null);
										}
									}
								}
							}
						}
					}
				}
			}
			for(Map.Entry<String, Field> ent : fldSrcMap.entrySet()) {
				// フィールド
				if(gsMtdsSrcMap.containsKey(ent.getKey())) {
					continue;
				}

				Field fldSrc = fldSrcMap.get(ent.getKey());
				if(gsMtdsDstMap.containsKey(ent.getKey())) {
					Method[] gsMtdsDst = gsMtdsDstMap.get(ent.getKey());
					Method smDst = gsMtdsDst[1];
					if(gsMtdsDst[0] != null && gsMtdsDst[1] != null) {
						Class<?>[] prmClssDst = smDst.getParameterTypes();
						if(prmClssDst.length == 1) {
							// フィールド→メソッドのコピーが可能

							Object valSrc = fldSrc.get(objSrc);
							String kpc = StringUtils.uncapitalize(smDst.getName().substring(3));
							if(valSrc != null) {
								Field fld = fldDstMap.get(ent.getKey());
								ParamSidePack psp = ParamSidePack.byConversionClass(gsMtdsDst, fld, ns + kpc, rpd);
								if(List.class.isAssignableFrom(prmClssDst[0])) {
									ParameterizedType gType = (ParameterizedType)smDst.getGenericParameterTypes()[0];
									Type[] aTypes = gType.getActualTypeArguments();
									if(!ParamSidePack.hasConversionClass(psp)) {
										List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass(), psp, ns + kpc + ".");
										smDst.invoke(objDst, lstDst);
									} else {
										List<String> lstDst1 = createListAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kpc + ".");
										ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), (Class<?>)aTypes[0]);
										List<Object> lstDst2 = new ArrayList<>();
										for(String str : lstDst1) {
											try {
												Object val = cub.convert(str, (Class<?>)aTypes[0]);
												lstDst2.add(val);
											} catch(ConversionException ce) {
												logger.info(ce.getMessage());
												lstDst2.add(null);
											}
										}
										smDst.invoke(objDst, lstDst2);
									}
								} else {
									if(!ParamSidePack.hasConversionClass(psp)) {
										Object valDst = createObjectAndCopyFlx(prmClssDst[0], valSrc, valSrc.getClass(), psp, ns + kpc + ".");
										smDst.invoke(objDst, valDst);
									} else {
										String valDst1 = createObjectAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kpc + ".");
										ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), prmClssDst[0]);
										try {
											Object valDst2 = cub.convert(valDst1, prmClssDst[0]);
											smDst.invoke(objDst, valDst2);
										} catch(ConversionException ce) {
											logger.info(ce.getMessage());
											smDst.invoke(objDst, new Object[] { null });
										}
									}
								}
							}
						}
					}
				} else if(fldDstMap.containsKey(ent.getKey())) {
					Field fldDst = fldDstMap.get(ent.getKey());
					// フィールド→フィールドのコピーが可能

					Object valSrc = fldSrc.get(objSrc);
					String kcm = fldDst.getName();
					if(valSrc != null) {
						ParamSidePack psp = ParamSidePack.byConversionClass(fldDst, ns + kcm, rpd);
						if(List.class.isAssignableFrom(fldDst.getType())) {
							ParameterizedType gType = (ParameterizedType)fldDst.getGenericType();
							Type[] aTypes = gType.getActualTypeArguments();
							if(!ParamSidePack.hasConversionClass(psp)) {
								List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass(), psp, ns + kcm + ".");
								fldDst.set(objDst, lstDst);
							} else {
								List<String> lstDst1 = createListAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kcm + ".");
								ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), (Class<?>)aTypes[0]);
								List<Object> lstDst2 = new ArrayList<>();
								for(String str : lstDst1) {
									try {
										Object val = cub.convert(str, (Class<?>)aTypes[0]);
										lstDst2.add(val);
									} catch(ConversionException ce) {
										logger.info(ce.getMessage());
										lstDst2.add(null);
									}
								}
								fldDst.set(objDst, lstDst2);
							}
						} else {
							if(!ParamSidePack.hasConversionClass(psp)) {
								Object valDst = createObjectAndCopyFlx(fldDst.getType(), valSrc, valSrc.getClass(), psp, ns + kcm + ".");
								fldDst.set(objDst, valDst);
							} else {
								String valDst1 = createObjectAndCopyFlx(String.class, valSrc, valSrc.getClass(), psp, ns + kcm + ".");
								ConvertUtilsBean cub = ReferenceConverter.getConvertUtils(psp.getConversionClass(), fldDst.getType());
								try {
									Object valDst2 = cub.convert(valDst1, fldDst.getType());
									fldDst.set(objDst, valDst2);
								} catch(ConversionException ce) {
									logger.info(ce.getMessage());
									fldDst.set(objDst, null);
								}
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> createListAndCopyFlx(Type typeDst, Object objSrc, Class<?> clsSrc, ParamSidePack psp, String ns)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		if(objSrc == null) {
			return null;
		}

		Class<T> clsDst;
		if(typeDst instanceof Class) {
			clsDst = (Class<T>)typeDst;
		} else {
			ParameterizedType gType = (ParameterizedType)typeDst;
			// Type[] aTypes = gType.getActualTypeArguments();
			clsDst = (Class<T>)gType.getRawType();
			// ToDo: ネストしたジェネリクスの考慮
		}

		List<Object> lstSrc = null;
		List<T> lstDst = new ArrayList<>();
		if(ReferenceStructure.class.isAssignableFrom(clsSrc)) {
			ReferenceStructure ref = (ReferenceStructure)objSrc;
			lstSrc = (List<Object>)ref.conv(clsDst).getList();
			if(lstSrc == null) {
				return null;
			} else if(lstSrc.size() == 0) {
				return lstDst;
			} else {
				objSrc = null;
				for(Object lo : lstSrc) {
					if(lo != null) {
						objSrc = lo;
						break;
					}
				}
				if(objSrc == null) {
					for(int li = 0; li < lstSrc.size(); li++) {
						lstDst.add(null);
					}
					return lstDst;
				}
				clsSrc = objSrc.getClass();
			}
			if(clsDst.isAssignableFrom(clsSrc)) {
				return (List<T>)lstSrc;
			}
		} else if(List.class.isAssignableFrom(clsSrc)) {
			lstSrc = (List<Object>)objSrc;
			if(lstSrc.size() == 0) {
				return lstDst;
			} else {
				int idx = 0;
				while(idx < lstSrc.size()) {
					objSrc = lstSrc.get(idx);
					if(objSrc != null) {
						break;
					}
					idx++;
				}
				if(idx == lstSrc.size()) {
					// 要素が全てnull
					return (List<T>)lstSrc;
				}
				clsSrc = objSrc.getClass();
				objSrc = lstSrc.get(0);
			}
			if(clsDst.isAssignableFrom(clsSrc)) {
				return (List<T>)lstSrc;
			}
		} else if(Map.class.isAssignableFrom(clsSrc)) {
			Map<?, ?> mapSrc = (Map<?, ?>)objSrc;
			for(Map.Entry<?, ?> ent : mapSrc.entrySet()) {
				objSrc = ent.getValue();
				clsSrc = objSrc.getClass();
				T valDst = createObjectAndCopyFlx(clsDst, objSrc, clsSrc, psp, ns + "1.");
				lstDst.add(valDst);
			}
			return lstDst;
		} else if(clsDst.isAssignableFrom(clsSrc)) {
			// ToDo: clsSrcにListが渡されてくるパターンってある？ 整理されてないような
			// lstDst.add((T)objSrc);
			lstDst.addAll((List<T>)objSrc);
			return lstDst;
		} else {
			T valDst = createObjectAndCopyFlx(clsDst, objSrc, clsSrc, psp, ns + "1.");
			lstDst.add(valDst);
			return lstDst;
		}

		copyListFlx(lstDst, clsDst, lstSrc, clsSrc, psp, ns);
		return lstDst;
	}

	private <T> void copyListFlx(List<T> lstDst, Class<T> clsDst, List<Object> lstSrc, Class<?> clsSrc, ParamSidePack psp, String ns)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		for(int idx = 0; idx < lstSrc.size(); idx++) {
			Object elmSrc = lstSrc.get(idx);
			if(elmSrc == null) {
				lstDst.add(null);
			} else {
				if(ReferenceStructure.class.isAssignableFrom(clsSrc)) {
					ReferenceStructure ref = (ReferenceStructure)elmSrc;
					psp = ParamSidePack.byConversionReference(ref, psp, ns + (idx + 1), rpd);
				}
				T elmDst = createObjectAndCopyFlx(clsDst, elmSrc, clsSrc, psp, ns + (idx + 1) + ".");
				lstDst.add(elmDst);
			}
		}
	}

	private void addDebugCacheTree(Cache cache, String indent, String msg) {
		if(!isLogDebugCache) return;
		// debugCacheTree.append(indent);
		debugCacheTree.append(StringUtils.repeat(" ", cacheTower.size()));
		debugCacheTree.append(cache.getKey());
		debugCacheTree.append(",");
		debugCacheTree.append(cache.getCreated().toString());
		debugCacheTree.append(",");
		debugCacheTree.append(cache.getExpires().toString());
		debugCacheTree.append(",");
		debugCacheTree.append(msg);
		debugCacheTree.append("\n");
	}

}
