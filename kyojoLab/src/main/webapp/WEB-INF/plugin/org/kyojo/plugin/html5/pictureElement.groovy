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

class PictureElement extends AnyHtmlElement {

	List<SourceElement> sources
	ImgElement img

	PictureElement() {}

	PictureElement(List<SourceElement> sources, ImgElement img) {
		this.sources = sources
		this.img = img
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachHtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachSources(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachImg(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachSources(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Sources." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, SourceElement.class).getType()
			List<SourceElement> sources = My.deminion(mnn, listType)
			if(sources != null) {
				this.sources = sources
				return true
			}
			return false
		})
	}

	boolean attachImg(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Img." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, ImgElement.class).getType()
			ImgElement img = My.deminion(mnn, listType)
			if(img != null) {
				this.img = img
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		sources != null || img != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseSources(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseImg(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseSources(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(sources == null) {
			return false
		} else {
			sources.each { source ->
				source.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	boolean parseImg(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(img == null) {
			return false
		} else {
			img.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<PictureElement> {

		private List<SourceElement> sources
		private ImgElement img

		Builder setSources(List<SourceElement> sources) {
			this.sources = sources
			return this
		}

		Builder setImg(ImgElement img) {
			this.img = img
			return this
		}

		@Override
		PictureElement build() {
			PictureElement obj = super.build()
			obj.sources = sources
			obj.img = img
			return obj
		}

	}

}
