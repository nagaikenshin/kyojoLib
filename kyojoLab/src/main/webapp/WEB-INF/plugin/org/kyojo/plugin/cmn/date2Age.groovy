package org.kyojo.plugin.cmn

import java.text.ParseException
import java.text.SimpleDateFormat

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

class date2Age {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		SimpleDateFormat sdfYMDHMSZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
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
				Calendar now = Calendar.getInstance()
				Calendar tgt = Calendar.getInstance()
				tgt.setTime(dt)

				int age = now.get(Calendar.YEAR) - tgt.get(Calendar.YEAR)
				if(now.get(Calendar.MONTH) < tgt.get(Calendar.MONTH)
						|| (now.get(Calendar.MONTH) == tgt.get(Calendar.MONTH)
							&& now.get(Calendar.DAY_OF_MONTH) < tgt.get(Calendar.DAY_OF_MONTH))) {
					age--;
				}

				cache.addLine("" + age)
			}
		}
		// cache.addLine("\n")

		return null
	}

}
