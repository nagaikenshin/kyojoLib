package org.kyojo.plugin.bs4Lab.checkoutLab

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

class FormatUSD {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(args.isInteger()) {
			int usd = args as Integer
			def sb = new StringBuilder(usd < 0 ? "-" : "")
			sb << '$'
			sb << (usd < 0 ? -usd : usd)
			sb << "\n"
			cache.addLine(sb.toString())
		}

		return null
	}

}
