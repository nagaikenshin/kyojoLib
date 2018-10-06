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

class TableElement extends AnyHtmlElement {

	CaptionElement caption
	List<ColGroupElement> colGroups
	THeadElement tHead
	TBodyElement tBody
	TFootElement tFoot
	List<TrElement> trs

	TableElement() {
	}

	TableElement(TBodyElement tBody) {
		this(null, null, null, tBody, null)
	}

	TableElement(CaptionElement caption, TBodyElement tBody) {
		this(caption, null, null, tBody, null)
	}

	TableElement(List<ColGroupElement> colGroups, TBodyElement tBody) {
		this(null, colGroups, null, tBody, null)
	}

	TableElement(CaptionElement caption, List<ColGroupElement> colGroups,
			TBodyElement tBody) {
		this(caption, colGroups, null, tBody, null)
	}

	TableElement(THeadElement tHead, TBodyElement tBody) {
		this(null, null, tHead, tBody, null)
	}

	TableElement(CaptionElement caption, THeadElement tHead, TBodyElement tBody) {
		this(caption, null, tHead, tBody, null)
	}

	TableElement(List<ColGroupElement> colGroups, THeadElement tHead, TBodyElement tBody) {
		this(null, colGroups, tHead, tBody, null)
	}

	TableElement(CaptionElement caption, List<ColGroupElement> colGroups,
			THeadElement tHead, TBodyElement tBody) {
		this(caption, colGroups, tHead, tBody, null)
	}

	TableElement(THeadElement tHead, TBodyElement tBody, TFootElement tFoot) {
		this(null, null, tHead, tBody, tFoot)
	}

	TableElement(CaptionElement caption,
			THeadElement tHead, TBodyElement tBody, TFootElement tFoot) {
		this(caption, null, tHead, tBody, tFoot)
	}

	TableElement(List<ColGroupElement> colGroups,
			THeadElement tHead, TBodyElement tBody, TFootElement tFoot) {
		this(null, colGroups, tHead, tBody, tFoot)
	}

	TableElement(CaptionElement caption, List<ColGroupElement> colGroups,
			THeadElement tHead, TBodyElement tBody, TFootElement tFoot) {
		this.caption = caption
		this.colGroups = colGroups
		this.tHead = tHead
		this.tBody = tBody
		this.tFoot = tFoot
	}

	TableElement(List<TrElement> trs) {
		this(null, null, trs)
	}

	TableElement(CaptionElement caption, List<TrElement> trs) {
		this(caption, null, trs)
	}

	TableElement(List<ColGroupElement> colGroups, List<TrElement> trs) {
		this(null, colGroups, trs)
	}

	TableElement(CaptionElement caption, List<ColGroupElement> colGroups,
			List<TrElement> trs) {
		this.caption = caption
		this.colGroups = colGroups
		this.trs = trs
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachHtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachCaption(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachColGroups(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachTHead(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachTBody(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachTFoot(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachTrs(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachCaption(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Caption." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, CaptionElement.class).getType()
			CaptionElement caption = My.deminion(mnn, listType)
			if(caption != null) {
				this.caption = caption
				return true
			}
			return false
		})
	}

	boolean attachColGroups(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"ColGroups." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, ColGroupElement.class).getType()
			List<ColGroupElement> colGroups = My.deminion(mnn, listType)
			if(colGroups != null) {
				this.colGroups = colGroups
				return true
			}
			return false
		})
	}

	boolean attachTHead(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"THead." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, THeadElement.class).getType()
			THeadElement tHead = My.deminion(mnn, listType)
			if(tHead != null) {
				this.tHead = tHead
				return true
			}
			return false
		})
	}

	boolean attachTBody(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"TBody." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, TBodyElement.class).getType()
			TBodyElement tBody = My.deminion(mnn, listType)
			if(tBody != null) {
				this.tBody = tBody
				return true
			}
			return false
		})
	}

	boolean attachTFoot(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"TFoot." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, TFootElement.class).getType()
			TFootElement tFoot = My.deminion(mnn, listType)
			if(tFoot != null) {
				this.tFoot = tFoot
				return true
			}
			return false
		})
	}

	boolean attachTrs(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Trs." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, TrElement.class).getType()
			List<TrElement> trs = My.deminion(mnn, listType)
			if(trs != null) {
				this.trs = trs
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		caption != null || colGroups != null ||
			tHead != null || tBody != null || tFoot != null ||
			trs != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseCaption(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseColGroups(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseTHead(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseTBody(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseTFoot(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseTrs(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseCaption(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(caption == null) {
			return false
		} else {
			caption.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseColGroups(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(colGroups == null) {
			return false
		} else {
			colGroups.each { colGroup ->
				colGroup.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	boolean parseTHead(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(tHead == null) {
			return false
		} else {
			tHead.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseTBody(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(tBody == null) {
			return false
		} else {
			tBody.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseTFoot(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(tFoot == null) {
			return false
		} else {
			tFoot.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseTrs(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(trs == null) {
			return false
		} else {
			trs.each { tr ->
				tr.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<TableElement> {

		private CaptionElement caption
		private List<ColGroupElement> colGroups
		private THeadElement tHead
		private TBodyElement tBody
		private TFootElement tFoot
		private List<TrElement> trs

		Builder setCaption(CaptionElement caption) {
			this.caption = caption
			return this
		}

		Builder setColGroups(List<ColGroupElement> colGroups) {
			this.colGroups = colGroups
			return this
		}

		Builder setTHead(THeadElement tHead) {
			this.tHead = tHead
			return this
		}

		Builder setTBody(TBodyElement tBody) {
			this.tBody = tBody
			return this
		}

		Builder setTFoot(TFootElement tFoot) {
			this.tFoot = tFoot
			return this
		}

		Builder setTrs(List<TrElement> trs) {
			this.trs = trs
			return this
		}

		@Override
		TableElement build() {
			TableElement obj = super.build()
			obj.caption = caption
			obj.colGroups = colGroups
			obj.tHead = tHead
			obj.tBody = tBody
			obj.tFoot = tFoot
			obj.trs = trs
			return obj
		}

	}

}
