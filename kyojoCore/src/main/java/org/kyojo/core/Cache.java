package org.kyojo.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cache implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String sKey = "";    // static key
	protected String dKey = "";    // dynamic key
	protected String eKey = "";    // extension key
	private static Pattern skpt = Pattern.compile("^(.+)__(.*)\\.(.+?)$");
	protected boolean flgSkipAfter = false;
	protected HashMap<String, String> childKeys = new HashMap<String, String>();
	private List<String> lines = new ArrayList<String>();
	private Time14 created = null;
	private Time14 expires = null;
	private HashSet<String> missFiles = new HashSet<String>();
	private HashSet<String> refFiles = new HashSet<String>();

	public Cache(String sKey, String dKey, String eKey, String created) {
		this(sKey, dKey, eKey, new Time14(created));
	}

	public Cache(String sKey, String dKey, String eKey, String created, String expires) {
		this(sKey, dKey, eKey, new Time14(created), new Time14(expires));
	}

	public Cache(String sKey, String dKey, String eKey, Time14 created) {
		this(sKey, dKey, eKey, created, Time14.FUTURE);
	}

	public Cache(String sKey, String dKey, String eKey, Time14 created, Time14 expires) {
		this.sKey = sKey;
		this.dKey = dKey;
		this.eKey = eKey;
		this.created = created;
		this.expires = expires;
	}

	public String getSKey() {
		return sKey;
	}

	public String getDKey() {
		return dKey;
	}

	public String getEKey() {
		return eKey;
	}

	public Time14 getCreated() {
		return created;
	}

	public Time14 getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		setExpires(new Time14(expires));
	}

	public void setExpires(Time14 expires) {
		this.expires = expires;
	}

	public static String concatKeys(String sKey, String dKey, String eKey) {
		return sKey + "__" + dKey + "." + eKey;
	}

	public static String[] splitKey(String key) {
		Matcher skmt = skpt.matcher(key);
		if(skmt.matches()) {
			return new String[] { skmt.group(1), skmt.group(2), skmt.group(3) };
		}
		return null;
	}

	public String getKey() {
		return concatKeys(sKey, dKey, eKey);
	}

	public boolean isSkipAfter() {
		return flgSkipAfter;
	}

	public void setSkipAfter(boolean flg) {
		flgSkipAfter = flg;
	}

	public void addChildKey(String key, Time14 expires) {
		childKeys.put(key, expires.toString());
		this.expires = this.expires.compareTo(expires) < 0 ? this.expires : expires;
	}

	public Iterator<String> getChildKeys() {
		return childKeys.keySet().iterator();
	}

	public void copyChildKeys(Cache cache) {
		for(Map.Entry<String, String> ent : childKeys.entrySet()) {
			cache.addChildKey(ent.getKey(), new Time14(ent.getValue()));
		}
	}

	public String getChildExpires(String key) {
		return childKeys.get(key);
	}

	public void addLine(String line) {
		lines.add(line);
	}

	public Iterator<String> getLines() {
		return lines.iterator();
	}

	public Iterator<String> getLinesAndSaveMemory() {
		List<String> tmpLines = lines;
		lines = new ArrayList<String>();
		lines.add("(deleted)");
		return tmpLines.iterator();
	}

	public void clearLines() {
		lines.clear();
	}

	public void addRefFile(String path) {
		refFiles.add(path);
	}

	public Iterator<String> getRefFiles() {
		return refFiles.iterator();
	}

	public void copyRefFiles(Cache cache) {
		for(String refFile : refFiles) {
			cache.addRefFile(refFile);
		}
	}

	public void addMissFile(String path) {
		missFiles.add(path);
	}

	public Iterator<String> getMissFiles() {
		return missFiles.iterator();
	}

	public void copyMissFiles(Cache cache) {
		for(String missFile : missFiles) {
			cache.addMissFile(missFile);
		}
	}

	public boolean isEmpty() {
		return lines.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String line : lines) {
			sb.append(line);
			// sb.append("\n");
		}
		return sb.toString();
	}

}
