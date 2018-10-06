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

class date2YmdhmJa {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		SimpleDateFormat sdfYMDHMSZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
		SimpleDateFormat sdfJaYMDHM = new SimpleDateFormat("yyyy年M月d日 HH:mm")

		if(StringUtils.isNotBlank(args)) {
			Date dt = null
			try {
				dt = sdfYMDHMSZ.parse(args)
			} catch(ParseException pe1) {
				try {
					dt = sdfYMDHMS.parse(args)
				} catch(ParseException pe2) {}
			}

			if(dt != null) {
				cache.addLine(sdfJaYMDHM.format(dt))
			}
		}
		// cache.addLine("\n")

		return null
	}

}
