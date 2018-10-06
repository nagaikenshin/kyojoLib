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

class ColGroupElement extends AnyXhtmlElement {

	List<ColElement> cols

	ColGroupElement() {
	}

	ColGroupElement(List<ColElement> cols) {
		this.cols = cols
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachXhtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachCols(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachCols(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Cols." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, ColElement.class).getType()
			List<ColElement> cols = My.deminion(mnn, listType)
			if(cols != null) {
				this.cols = cols
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		cols != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseCols(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) ||
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseCols(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(cols == null) {
			return false
		} else {
			cols.each { col ->
				col.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	public static class Builder extends org.kyojo.plugin.xml10.xhtml11.XhtmlElementImpl.Builder<ColGroupElement> {

		private List<ColElement> cols

		Builder setCols(List<ColElement> cols) {
			this.cols = cols
			return this
		}

		@Override
		ColGroupElement build() {
			ColGroupElement obj = super.build()
			obj.cols = cols
			return obj
		}

	}

}
