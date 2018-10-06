package org.kyojo.plugin.html5

import java.lang.reflect.Type

import org.kyojo.core.Cache
import org.kyojo.core.Constants
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.gson.reflect.TypeToken
import org.kyojo.minion.My

class DetailsElement extends AnyHtmlElement {

	SummaryElement summary

	DetailsElement() {}

	DetailsElement(SummaryElement summary) {
		this.summary = summary
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachHtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachSummary(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachSummary(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Summary." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, SummaryElement.class).getType()
			SummaryElement summary = My.deminion(mnn, listType)
			if(summary != null) {
				this.summary = summary
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		summary != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseSummary(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseSummary(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(summary == null) {
			return false
		} else {
			summary.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<DetailsElement> {

		private SummaryElement summary

		Builder setSummary(SummaryElement summary) {
			this.summary = summary
			return this
		}

		@Override
		DetailsElement build() {
			DetailsElement obj = super.build()
			obj.summary = summary
			return obj
		}

	}

}
