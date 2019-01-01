package org.kyojo.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.io.IOLayer;
import org.kyojo.minion.My;
import org.kyojo.schemaorg.SimpleJsonBuilder;

public final class SessionData implements Map<String, Object> {

	private static final Log logger = LogFactory.getLog(SessionData.class);

	public static final String SESSION_DATA_KEY_PREFIX = "kyojo-ssd-";

	private HttpServletRequest req = null;

	private HttpSession ssn = null;

	private String sid = null;

	private GlobalData gbd = null;

	private boolean isLogSaveAndLoadResult = false;

	public SessionData(HttpServletRequest req, GlobalData gbd) {
		this.req = req;
		this.gbd = gbd;
		IOLayer ioLayer = gbd.get(IOLayer.class);
		ssn = ioLayer.getSession(true, req);

		Object val = gbd.get("IS_LOG_SAVE_AND_LOAD_RESULT");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogSaveAndLoadResult = true;
		}
	}

	public void sessionRestart() {
		if(ssn != null) {
			ssn.invalidate();
			sid = null;
		}
		IOLayer ioLayer = gbd.get(IOLayer.class);
		ssn = ioLayer.getSession(true, req);
	}

	public String getSessionID() {
		if(sid != null) {
			return sid;
		}
		if(ssn == null) {
			IOLayer ioLayer = gbd.get(IOLayer.class);
			logger.trace("getSession start.");
			ssn = ioLayer.getSession(true, req);
			logger.trace("getSession end.");
		}

		String sid0 = ssn.getId();
		sid = StringUtils.isEmpty(sid0) ? "" : My.hs(sid0);
		return sid;
	}

	@Override
	public int size() {
		if(ssn == null) {
			IOLayer ioLayer = gbd.get(IOLayer.class);
			logger.trace("getSession start.");
			ssn = ioLayer.getSession(true, req);
			logger.trace("getSession end.");
		}

		logger.trace("ssd size start.");
		Enumeration<String> keys = ssn.getAttributeNames();
		int size = 0;
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.startsWith(SESSION_DATA_KEY_PREFIX)) {
				size++;
			}
		}
		logger.trace("ssd size end.");

		return size;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) == null;
	}

	@Override
	public boolean containsValue(Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(Object key) {
		if(ssn == null) {
			IOLayer ioLayer = gbd.get(IOLayer.class);
			logger.trace("getSession start.");
			ssn = ioLayer.getSession(true, req);
			logger.trace("getSession end.");
		}

		logger.trace("ssd get start: " + key);
		Object obj = ssn.getAttribute(SESSION_DATA_KEY_PREFIX + key);
		logger.trace("ssd get end: " + key);
		return obj;
	}

	@Override
	public Object put(String key, Object val) {
		if(ssn == null) {
			IOLayer ioLayer = gbd.get(IOLayer.class);
			logger.trace("getSession start.");
			ssn = ioLayer.getSession(true, req);
			logger.trace("getSession end.");
		}

		logger.trace("ssd put start: " + key);
		ssn.setAttribute(SESSION_DATA_KEY_PREFIX + key, val);
		logger.trace("ssd put end: " + key);
		return val;
	}

	@Override
	public Object remove(Object key) {
		if(ssn == null) {
			IOLayer ioLayer = gbd.get(IOLayer.class);
			logger.trace("getSession start.");
			ssn = ioLayer.getSession(true, req);
			logger.trace("getSession end.");
		}

		Object val = get(key);
		logger.trace("ssd remove start: " + key);
		ssn.removeAttribute(SESSION_DATA_KEY_PREFIX + key);
		logger.trace("ssd remove end: " + key);
		return val;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		for(Map.Entry<? extends String, ? extends Object> ent : map.entrySet()) {
			put(ent.getKey(), ent.getValue());
		}
	}

	@Override
	public void clear() {
		if(ssn == null) {
			IOLayer ioLayer = gbd.get(IOLayer.class);
			logger.trace("getSession start.");
			ssn = ioLayer.getSession(true, req);
			logger.trace("getSession end.");
		}

		Enumeration<String> keys = ssn.getAttributeNames();
		Set<String> rmvKeys = new HashSet<>();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.startsWith(SESSION_DATA_KEY_PREFIX)) {
				rmvKeys.add(key);
			}
		}

		logger.trace("ssd clear start.");
		for(String rmvKey : rmvKeys) {
			ssn.removeAttribute(rmvKey);
		}
		logger.trace("ssd clear end.");
	}

	@Override
	public Set<String> keySet() {
		if(ssn == null) {
			IOLayer ioLayer = gbd.get(IOLayer.class);
			logger.trace("getSession start.");
			ssn = ioLayer.getSession(true, req);
			logger.trace("getSession end.");
		}

		logger.trace("ssd keySet start.");
		Enumeration<String> keys = ssn.getAttributeNames();
		logger.trace("ssd keySet end.");
		Set<String> keySet = new HashSet<>();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.startsWith(SESSION_DATA_KEY_PREFIX)) {
				keySet.add(key.substring(SESSION_DATA_KEY_PREFIX.length()));
			}
		}

		return keySet;
	}

	@Override
	public Collection<Object> values() {
		if(ssn == null) {
			IOLayer ioLayer = gbd.get(IOLayer.class);
			logger.trace("getSession start.");
			ssn = ioLayer.getSession(true, req);
			logger.trace("getSession end.");
		}

		logger.trace("ssd values start.");
		Enumeration<String> keys = ssn.getAttributeNames();
		Set<Object> values = new HashSet<>();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.startsWith(SESSION_DATA_KEY_PREFIX)) {
				values.add(ssn.getAttribute(key));
			}
		}
		logger.trace("ssd values end.");

		return values;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		if(ssn == null) {
			IOLayer ioLayer = gbd.get(IOLayer.class);
			logger.trace("getSession start.");
			ssn = ioLayer.getSession(true, req);
			logger.trace("getSession end.");
		}

		logger.trace("ssd entrySet start.");
		Enumeration<String> keys = ssn.getAttributeNames();
		Map<String, Object> map = new HashMap<>();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.startsWith(SESSION_DATA_KEY_PREFIX)) {
				map.put(key.substring(SESSION_DATA_KEY_PREFIX.length()), ssn.getAttribute(key));
			}
		}
		logger.trace("ssd entrySet end.");

		return map.entrySet();
	}

	public HttpServletRequest getRequest() {
		return req;
	}

	public GlobalData getGlobalData() {
		return gbd;
	}

	public void turnOver(Object obj, String sKey, String... argAry) {
		String mnn = SimpleJsonBuilder.toJson(obj);
		String args = sid;
		if(argAry.length > 0) {
			args = sid + "," + StringUtils.join(argAry, ",");
		}
		Cache mc = new Cache(sKey, My.hs(args), "mnn", new Time14());
		mc.addLine(mnn);
		if(isLogSaveAndLoadResult) {
			if(args == null) {
				logger.debug("args: (null)");
			} else {
				logger.debug("args: \"" + args + "\"");
			}
			logger.debug("ssd-store: " + mc.getKey() + " -> " + mnn);
		}
		put(mc.getKey(), mc);
	}

	public <T> T takeOver(Class<?> cls, String sKey, String... argAry) {
		String args = sid;
		if(argAry.length > 0) {
			args = sid + "," + StringUtils.join(argAry, ",");
		}
		String key = Cache.concatKeys(sKey, My.hs(args), "mnn");
		Cache mc = (Cache)get(key);
		String mnn = null;
		if(mc != null) {
			Iterator<String> itr = mc.getLines();
			if(itr.hasNext()) {
				mnn = itr.next();
			}
		}
		if(isLogSaveAndLoadResult) {
			if(args == null) {
				logger.debug("args: (null)");
			} else {
				logger.debug("args: \"" + args + "\"");
			}
			logger.debug("ssd-draw: " + key + " -> " + mnn);
		}
		if(mnn != null) {
			try {
				return My.deminion(mnn, cls);
			} catch(Exception ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}

		return null;
	}

	public void takeOver(Object obj, String sKey, String... argAry) {
		Object obj2 = takeOver(obj.getClass(), sKey, argAry);
		try {
			BeanUtils.copyProperties(obj, obj2);
		} catch(IllegalAccessException iae) {
			logger.warn(iae.getMessage(), iae);
		} catch(InvocationTargetException ite) {
			logger.warn(ite.getMessage(), ite);
		}
	}
}
