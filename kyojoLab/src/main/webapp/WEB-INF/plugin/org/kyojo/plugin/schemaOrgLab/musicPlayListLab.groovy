package org.kyojo.plugin.schemaOrgLab

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.schemaorg.SimpleJsonBuilder
import org.kyojo.schemaorg.m3n4.core.Clazz.MusicPlaylist
import org.kyojo.schemaorg.m3n4.core.impl.AUTHOR
import org.kyojo.schemaorg.m3n4.core.impl.GENDER
import org.kyojo.schemaorg.m3n4.core.impl.GENDER_TYPE
import org.kyojo.schemaorg.m3n4.core.impl.MUSIC_PLAYLIST
import org.kyojo.schemaorg.m3n4.core.impl.PERSON

class MusicPlayListLab {

	MusicPlaylist musicPlaylist
	transient String jsonLd

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		recycle(args, gbd, ssd, rqd, rpd)
		return null
	}

	Object recycle(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(musicPlaylist == null) {
			musicPlaylist = new MUSIC_PLAYLIST(new AUTHOR(new PERSON(new GENDER(new GENDER_TYPE("unanswered")))))
		}
		return null
	}

	Object doSubmit(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		jsonLd = SimpleJsonBuilder.toJsonLd(musicPlaylist, MusicPlaylist.class)
		return null
	}

}
