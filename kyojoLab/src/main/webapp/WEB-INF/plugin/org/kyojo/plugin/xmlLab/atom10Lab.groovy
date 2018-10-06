package org.kyojo.plugin.xmlLab

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.plugin.xml10.atom10.AuthorElement
import org.kyojo.plugin.xml10.atom10.ContentElement
import org.kyojo.plugin.xml10.atom10.ContributorElement
import org.kyojo.plugin.xml10.atom10.EmailElement
import org.kyojo.plugin.xml10.atom10.EntryElement
import org.kyojo.plugin.xml10.atom10.FeedElement
import org.kyojo.plugin.xml10.atom10.GeneratorElement
import org.kyojo.plugin.xml10.atom10.IdElement
import org.kyojo.plugin.xml10.atom10.LinkElement
import org.kyojo.plugin.xml10.atom10.NameElement
import org.kyojo.plugin.xml10.atom10.PublishedElement
import org.kyojo.plugin.xml10.atom10.RightsElement
import org.kyojo.plugin.xml10.atom10.TitleElement
import org.kyojo.plugin.xml10.atom10.UpdatedElement
import org.kyojo.plugin.xml10.atom10.UriElement
import org.kyojo.plugin.xml10.xhtml11.DivElement
import org.kyojo.plugin.xml10.xhtml11.IElement
import org.kyojo.plugin.xml10.xhtml11.PElement

class Atom10Lab {

	FeedElement feed

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {

		feed = new FeedElement.Builder().setAttrs([ xmlns: "http://www.w3.org/2005/Atom" ])\
			.setTitle(new TitleElement.Builder().setAttrs([ type: "text" ]).setText("dive into mark").build())\
			.setUpdated(new UpdatedElement.Builder().setText("2005-07-31T12:29:29Z").build())\
			.setId(new IdElement.Builder().setText("tag:example.org,2003:3").build())\
			.setLinks([
					new LinkElement.Builder().setAttrs([
							rel: "alternate", type: "text/html", hreflang: "en", href: "http://example.org/"
						]).build(),
					new LinkElement.Builder().setAttrs([
							rel: "self", type: "application/atom+xml", href: "http://example.org/feed.atom"
						]).build()
				])\
			.setRights(new RightsElement.Builder().setText("Copyright (c) 2003, Mark Pilgrim").build())\
			.setGenerator(new GeneratorElement.Builder().setAttrs([
					uri: "http://www.example.com/", version: "1.0"
				]).setText("Example Toolkit").build())\
			.setEntries([
					new EntryElement.Builder()\
						.setTitle(new TitleElement.Builder().setText("Atom draft-07 snapshot").build())\
						.setLinks([
								new LinkElement.Builder().setAttrs([
										rel: "alternate", type: "text/html", href: "http://example.org/2005/04/02/atom"
									]).build(),
								new LinkElement.Builder().setAttrs([
										rel: "enclosure", type: "audio/mpeg", length: "1337",
											href: "http://example.org/audio/ph34r_my_podcast.mp3"
									]).build()
							])\
						.setId(new IdElement.Builder().setText("tag:example.org,2003:3.2397").build())\
						.setUpdated(new UpdatedElement.Builder().setText("2005-07-31T12:29:29Z").build())\
						.setPublished(new PublishedElement.Builder().setText("2003-12-13T08:29:29-04:00").build())\
						.setAuthors([
								new AuthorElement.Builder()\
									.setName(new NameElement.Builder().setText("Mark Pilgrim").build())\
									.setUri(new UriElement.Builder().setText("http://example.org/").build())\
									.setEmail(new EmailElement.Builder().setText("f8dy@example.com").build())\
									.build()
							])\
						.setContributors([
								new ContributorElement.Builder()\
									.setName(new NameElement.Builder().setText("Sam Ruby").build())\
									.build(),
								new ContributorElement.Builder()\
									.setName(new NameElement.Builder().setText("Joe Gregorio").build())\
									.build()
							])\
						.setContent(new ContentElement.Builder().setAttrs([
									type: "xhtml", "xml:lang": "en", "xml:base": "http://diveintomark.org/"
								]).setNodes([
									new DivElement.Builder().setAttrs([
											xmlns: "http://www.w3.org/1999/xhtml"
										]).setNodes([
											new PElement.Builder().setNodes([
													new IElement.Builder().setText("[Update: The Atom draft is finished.]").build()
												]).build()
										]).build()
								]).build()
							)
						.build()
				])
			.build()

		return null
	}

}
