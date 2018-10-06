package org.kyojo.core;

import java.text.DateFormat;
import java.util.Date;

public class PluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public enum Level {

		OFF(0),
		FATAL(100),
		ERROR(200),
		WARN(300),
		INFO(400),
		DEBUG(500),
		TRACE(600),
		ALL(Integer.MAX_VALUE);

		private final int intLevel;

		Level(final int val) {
			intLevel = val;
		}

		public int intLevel() {
			return intLevel;
		}

	}

	private Object plgObj;
	private Class<?> plgCls;
	private String shortSID;
	private Date created;
	private GlobalData gbd;
	private SessionData ssd;
	private RequestData rqd;
	private ResponseData rpd;
	private Level level;

	public PluginException(Object plgObj, Class<?> plgCls, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			String message, Throwable cause, Level level) {
		super(message, cause);

		this.plgObj = plgObj;
		this.plgCls = plgCls;
		if(ssd == null) {
			shortSID = null;
		} else if(ssd.getSessionID().length() < 10) {
			shortSID = ssd.getSessionID();
		} else {
			shortSID = ssd.getSessionID().substring(0, 10);
		}
		created = new Date();
		this.gbd = gbd;
		this.ssd = ssd;
		this.rqd = rqd;
		this.rpd = rpd;
		this.level = level;
	}

	public Object getPluginObject() {
		return plgObj;
	}

	public Class<?> getPluginClass() {
		return plgCls;
	}

	public String getShortSID() {
		return shortSID;
	}

	public Date getCreated() {
		return created;
	}

	public GlobalData getGbd() {
		return gbd;
	}

	public SessionData getSsd() {
		return ssd;
	}

	public RequestData getRqd() {
		return rqd;
	}

	public ResponseData getRpd() {
		return rpd;
	}

	public Level getLevel() {
		return level;
	}

	public String getCreatedStr() {
		DateFormat dt = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		return dt.format(created);
	}

}
