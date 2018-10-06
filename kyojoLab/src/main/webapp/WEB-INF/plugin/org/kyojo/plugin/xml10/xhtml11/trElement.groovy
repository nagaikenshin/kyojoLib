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

class TrElement extends AnyXhtmlElement {

	List<TdElement> tds

	TrElement() {
	}

	TrElement(List<TdElement> tds) {
		this.tds = tds
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachXhtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachTds(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachTds(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Tds." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, TdElement.class).getType()
			List<TdElement> tds = My.deminion(mnn, listType)
			if(tds != null) {
				this.tds = tds
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		tds != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseTds(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) ||
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseTds(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(tds == null) {
			return false
		} else {
			tds.each { td ->
				td.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	public static class Builder extends org.kyojo.plugin.xml10.xhtml11.XhtmlElementImpl.Builder<TrElement> {

		private List<TdElement> tds

		Builder setTds(List<TdElement> tds) {
			this.tds = tds
			return this
		}

		@Override
		TrElement build() {
			TrElement obj = super.build()
			obj.tds = tds
			return obj
		}

	}

}
