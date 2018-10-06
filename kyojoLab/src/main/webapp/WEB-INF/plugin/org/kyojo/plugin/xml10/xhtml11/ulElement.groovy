package org.kyojo.plugin.xml10.xhtml11

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

class UlElement extends AnyXhtmlElement {

	List<LiElement> lis

	UlElement() {
	}

	UlElement(List<LiElement> lis) {
		this.lis = lis
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachXhtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachLis(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachLis(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Lis." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, LiElement.class).getType()
			List<LiElement> lis = My.deminion(mnn, listType)
			if(lis != null) {
				this.lis = lis
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		lis != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseLis(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) ||
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseLis(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(lis == null) {
			return false
		} else {
			lis.each { li ->
				li.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	public static class Builder extends org.kyojo.plugin.xml10.xhtml11.XhtmlElementImpl.Builder<UlElement> {

		private List<LiElement> lis

		Builder setLis(List<LiElement> lis) {
			this.lis = lis
			return this
		}

		@Override
		UlElement build() {
			UlElement obj = super.build()
			obj.lis = lis
			return obj
		}

	}

}
