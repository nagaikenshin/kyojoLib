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

class DlElement extends AnyXhtmlElement {

	List<DdElement> dds

	DlElement() {}

	DlElement(List<DdElement> dds) {
		this.dds = dds
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachXhtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachDds(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachDds(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Dds." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, DdElement.class).getType()
			List<DdElement> dds = My.deminion(mnn, listType)
			if(dds != null) {
				this.dds = dds
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		dds != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseDds(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseDds(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(dds == null) {
			return false
		} else {
			dds.each { dd ->
				dd.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	public static class Builder extends org.kyojo.plugin.xml10.xhtml11.XhtmlElementImpl.Builder<DlElement> {

		private List<DdElement> dds

		Builder setDds(List<DdElement> dds) {
			this.dds = dds
			return this
		}

		@Override
		DlElement build() {
			DlElement obj = super.build()
			obj.dds = dds
			return obj
		}

	}

}
