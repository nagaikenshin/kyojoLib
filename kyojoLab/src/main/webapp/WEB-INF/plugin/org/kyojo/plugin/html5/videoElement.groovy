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

class VideoElement extends AnyHtmlElement {

	List<TrackElement> tracks
	List<SourceElement> sources

	VideoElement() {}

	VideoElement(List<TrackElement> tracks, List<SourceElement> sources) {
		this.tracks = tracks
		this.sources = sources
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachHtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachTracks(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachSources(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachTracks(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Tracks." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, TrackElement.class).getType()
			List<TrackElement> tracks = My.deminion(mnn, listType)
			if(tracks != null) {
				this.tracks = tracks
				return true
			}
			return false
		})
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

	@Override
	boolean hasChildren() {
		tracks != null || sources != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseTracks(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseSources(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseTracks(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(tracks == null) {
			return false
		} else {
			tracks.each { track ->
				track.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
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

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<VideoElement> {

		private List<TrackElement> tracks
		private List<SourceElement> sources

		Builder setTracks(List<TrackElement> tracks) {
			this.tracks = tracks
			return this
		}

		Builder setSources(List<SourceElement> sources) {
			this.sources = sources
			return this
		}

		@Override
		VideoElement build() {
			VideoElement obj = super.build()
			obj.tracks = tracks
			obj.sources = sources
			return obj
		}

	}

}
