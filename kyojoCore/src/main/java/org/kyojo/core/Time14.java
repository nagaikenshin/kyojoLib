package org.kyojo.core;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time14 implements Comparable<Time14>, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String MILLENNIUM = "+3";
	public static final Time14 OLD    = new Time14("00000000000000");
	public static final Time14 FUTURE = new Time14("99999999999999");
	private String str = null;

	public Time14() {
		this(new Date());
	}

	public Time14(Date date) {
		SimpleDateFormat form14 = new SimpleDateFormat("yyyyMMddHHmmss");
		String time14 = form14.format(date);
		init(time14);
	}

	public Time14(String str) {
		init(str);
	}

	private void init(String str) {
		this.str = str;
	}

	public int compareTo(String str2) {
		return str.compareTo(str2);
	}

	public int compareTo(Time14 t) {
		return str.compareTo(t.toString());
	}

	@Override
	public String toString() {
		return str;
	}
}
