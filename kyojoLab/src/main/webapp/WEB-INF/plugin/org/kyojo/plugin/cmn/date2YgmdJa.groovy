package org.kyojo.plugin.cmn

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import org.apache.commons.lang3.StringUtils

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.minion.My

class Date2YgmdJa {

	static final Gengo[] GENGOS = [
		new Gengo("平成", 1989, 1, 8),
		new Gengo("昭和", 1926, 12, 25),
		new Gengo("大正", 1912, 7, 30),
		new Gengo("明治", 1868, 1, 1),
		new Gengo("", Integer.MIN_VALUE, 0, 0)
	]

	static String getGengo(Calendar cal) {
		if(cal == null) return ""

		def year = cal.get(Calendar.YEAR)
		def month = cal.get(Calendar.MONTH) + 1
		def day = cal.get(Calendar.DAY_OF_MONTH)

		getGengo(year, month, day)
	}

	static String getGengo(LocalDate ld) {
		if(ld == null) return ""

		def year = ld.year
		def month = ld.monthValue
		def day = ld.dayOfMonth

		getGengo(year, month, day)
	}

	static String getGengo(int year, int month, int day) {
		for(int gi = 0; gi < GENGOS.length - 1; gi++) {
			Gengo gengo1 = GENGOS[gi]
			Gengo gengo2 = GENGOS[gi + 1]
			if(year > gengo1.startYear) {
				return gengo1.name + (year - gengo1.startYear + 1)
			} else if(year == gengo1.startYear) {
				if(month > gengo1.startMonth || (month == gengo1.startMonth && day >= gengo1.startDay)) {
					return gengo1.name + "元"
				} else {
					return gengo2.name + (year - gengo2.startYear + 1)
				}
			}
		}

		return ""
	}

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		SimpleDateFormat sdfYMDHMSZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
		SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd")

		if(StringUtils.isNotBlank(args)) {
			Date dt = null
			try {
				dt = sdfYMDHMSZ.parse(args)
			} catch(ParseException pe1) {
				try {
					dt = sdfYMDHMS.parse(args)
				} catch(ParseException pe2) {
					try {
						dt = sdfYMD.parse(args)
					} catch(ParseException pe3) {}
				}
			}

			if(dt != null) {
				Calendar cal = Calendar.getInstance()
				cal.setTime(dt)
				StringBuilder sb = new StringBuilder();
				sb.append(cal.get(Calendar.YEAR))
				sb.append("（")
				sb.append(getGengo(cal))
				sb.append("）年")
				sb.append(cal.get(Calendar.MONTH) + 1)
				sb.append("月")
				sb.append(cal.get(Calendar.DAY_OF_MONTH))
				sb.append("日")
				cache.addLine(sb.toString())
			}
		}
		// cache.addLine("\n")

		return null
	}

}

class Gengo {

	String name
	int startYear
	int startMonth
	int startDay

	Gengo(String name, int startYear, int startMonth, int startDay) {
		this.name = name
		this.startYear = startYear
		this.startMonth = startMonth
		this.startDay = startDay
	}

}

