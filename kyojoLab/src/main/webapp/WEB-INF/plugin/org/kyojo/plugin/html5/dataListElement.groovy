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

class DataListElement extends AnyHtmlElement {

	List<OptionElement> options

	DataListElement() {}

	DataListElement(List<OptionElement> options) {
		this.options = options
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachHtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachOptions(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachOptions(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Options." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, OptionElement.class).getType()
			List<OptionElement> options = My.deminion(mnn, listType)
			if(options != null) {
				this.options = options
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		options != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseOptions(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseOptions(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(options == null) {
			return false
		} else {
			options.each { option ->
				option.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<DataListElement> {

		private List<OptionElement> options

		Builder setOptions(List<OptionElement> options) {
			this.options = options
			return this
		}

		@Override
		DataListElement build() {
			DataListElement obj = super.build()
			obj.options = options
			return obj
		}

	}

}
