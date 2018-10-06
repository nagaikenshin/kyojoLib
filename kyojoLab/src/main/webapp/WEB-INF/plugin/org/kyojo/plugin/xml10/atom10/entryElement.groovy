package org.kyojo.plugin.xml10.atom10

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
import org.kyojo.plugin.xml10.XmlElementImpl

class EntryElement extends AnyXmlElement {

	List<AuthorElement> authors
	List<CategoryElement> categories
	ContentElement content
	List<ContributorElement> contributors
	IdElement id
	List<LinkElement> links
	PublishedElement published
	RightsElement rights
	SourceElement source
	SummaryElement summary
	TitleElement title
	UpdatedElement updated

	EntryElement() {}

	EntryElement(List<AuthorElement> authors, List<CategoryElement> categories,
			ContentElement content, List<ContributorElement> contributors, IdElement id,
			List<LinkElement> links, PublishedElement published, RightsElement rights,
			SourceElement source, SummaryElement summary, TitleElement title,
			UpdatedElement updated) {
		this.authors = authors
		this.categories = categories
		this.content = content
		this.contributors = contributors
		this.id = id
		this.links = links
		this.published = published
		this.rights = rights
		this.source = source
		this.summary = summary
		this.title = title
		this.updated = updated
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachXmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachAuthors(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachCategories(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachContent(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachContributors(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachId(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachLinks(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachPublished(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachRights(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachSource(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachSummary(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachTitle(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachUpdated(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachAuthors(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Authors." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, AuthorElement.class).getType()
			List<AuthorElement> authors = My.deminion(mnn, listType)
			if(authors != null) {
				this.authors = authors
				return true
			}
			return false
		})
	}

	boolean attachCategories(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Categories." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, CategoryElement.class).getType()
			List<CategoryElement> categories = My.deminion(mnn, listType)
			if(categories != null) {
				this.categories = categories
				return true
			}
			return false
		})
	}

	boolean attachContent(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Content." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, ContentElement.class).getType()
			ContentElement content = My.deminion(mnn, listType)
			if(content != null) {
				this.content = content
				return true
			}
			return false
		})
	}

	boolean attachContributors(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Contributors." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, ContributorElement.class).getType()
			List<ContributorElement> contributors = My.deminion(mnn, listType)
			if(contributors != null) {
				this.contributors = contributors
				return true
			}
			return false
		})
	}

	boolean attachId(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Id." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, IdElement.class).getType()
			IdElement id = My.deminion(mnn, listType)
			if(id != null) {
				this.id = id
				return true
			}
			return false
		})
	}

	boolean attachLinks(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Links." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, LinkElement.class).getType()
			List<LinkElement> links = My.deminion(mnn, listType)
			if(links != null) {
				this.links = links
				return true
			}
			return false
		})
	}

	boolean attachPublished(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Published." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, PublishedElement.class).getType()
			PublishedElement published = My.deminion(mnn, listType)
			if(published != null) {
				this.published = published
				return true
			}
			return false
		})
	}

	boolean attachRights(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Rights." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, RightsElement.class).getType()
			RightsElement rights = My.deminion(mnn, listType)
			if(rights != null) {
				this.rights = rights
				return true
			}
			return false
		})
	}

	boolean attachSource(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Source." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, SourceElement.class).getType()
			SourceElement source = My.deminion(mnn, listType)
			if(source != null) {
				this.source = source
				return true
			}
			return false
		})
	}

	boolean attachSummary(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Summary." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, SummaryElement.class).getType()
			SummaryElement summary = My.deminion(mnn, listType)
			if(summary != null) {
				this.summary = summary
				return true
			}
			return false
		})
	}

	boolean attachTitle(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Title." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, TitleElement.class).getType()
			TitleElement title = My.deminion(mnn, listType)
			if(title != null) {
				this.title = title
				return true
			}
			return false
		})
	}

	boolean attachUpdated(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Updated." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, UpdatedElement.class).getType()
			UpdatedElement updated = My.deminion(mnn, listType)
			if(updated != null) {
				this.updated = updated
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		authors != null || categories != null || content != null || contributors != null ||
			id != null || links != null || published != null || rights != null ||
			source != null || summary != null || title != null || updated != null ||
			super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseAuthors(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseCategories(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseContent(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseContributors(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseId(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseLinks(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parsePublished(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseRights(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseSource(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseSummary(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseTitle(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseUpdated(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseAuthors(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(authors == null) {
			return false
		} else {
			authors.each { author ->
				author.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	boolean parseCategories(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(categories == null) {
			return false
		} else {
			categories.each { category ->
				category.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	boolean parseContent(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(content == null) {
			return false
		} else {
			content.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseContributors(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(contributors == null) {
			return false
		} else {
			contributors.each { contributor ->
				contributor.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	boolean parseId(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(id == null) {
			return false
		} else {
			id.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseLinks(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(links == null) {
			return false
		} else {
			links.each { link ->
				link.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	boolean parsePublished(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(published == null) {
			return false
		} else {
			published.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseRights(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(rights == null) {
			return false
		} else {
			rights.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseSource(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(source == null) {
			return false
		} else {
			source.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseSummary(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(summary == null) {
			return false
		} else {
			summary.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseTitle(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(title == null) {
			return false
		} else {
			title.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseUpdated(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(updated == null) {
			return false
		} else {
			updated.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	public static class Builder extends XmlElementImpl.Builder<EntryElement> {

		private List<AuthorElement> authors
		private List<CategoryElement> categories
		private ContentElement content
		private List<ContributorElement> contributors
		private IdElement id
		private List<LinkElement> links
		private PublishedElement published
		private RightsElement rights
		private SourceElement source
		private SummaryElement summary
		private TitleElement title
		private UpdatedElement updated

		Builder setAuthors(List<AuthorElement> authors) {
			this.authors = authors
			return this
		}

		Builder setCategories(List<CategoryElement> categories) {
			this.categories = categories
			return this
		}

		Builder setContent(ContentElement content) {
			this.content = content
			return this
		}

		Builder setContributors(List<ContributorElement> contributors) {
			this.contributors = contributors
			return this
		}

		Builder setId(IdElement id) {
			this.id = id
			return this
		}

		Builder setLinks(List<LinkElement> links) {
			this.links = links
			return this
		}

		Builder setPublished(PublishedElement published) {
			this.published = published
			return this
		}

		Builder setRights(RightsElement rights) {
			this.rights = rights
			return this
		}

		Builder setSource(SourceElement source) {
			this.source = source
			return this
		}

		Builder setSummary(SummaryElement summary) {
			this.summary = summary
			return this
		}

		Builder setTitle(TitleElement title) {
			this.title = title
			return this
		}

		Builder setUpdated(UpdatedElement updated) {
			this.updated = updated
			return this
		}

		@Override
		EntryElement build() {
			EntryElement obj = super.build()
			obj.authors = authors
			obj.categories = categories
			obj.content = content
			obj.contributors = contributors
			obj.id = id
			obj.links = links
			obj.published = published
			obj.rights = rights
			obj.source = source
			obj.summary = summary
			obj.title = title
			obj.updated = updated
			return obj
		}

	}

}
