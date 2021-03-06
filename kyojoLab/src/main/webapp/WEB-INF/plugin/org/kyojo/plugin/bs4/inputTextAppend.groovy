package org.kyojo.plugin.bs4

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
import org.kyojo.plugin.html5.HtmlElement

class InputTextAppend extends InputTextNormal {

	HtmlElement append

	void giveAppendDefaults() {
		if(append != null) {
			if(StringUtils.isBlank(append.name) || append.name == append.rootName) {
				append.name = "span"
			}

			if(StringUtils.isBlank(append.defaultClass)) {
				append.defaultClass = "input-group-text"
			}
		}
	}

	@Override
	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		super.buildCache(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)

		giveAppendDefaults()

		return null
	}

}
