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

class FeedElement extends AnyXmlElement {

	List<AuthorElement> authors
	List<CategoryElement> categories
	List<ContributorElement> contributors
	GeneratorElement generator
	IconElement icon
	IdElement id
	List<LinkElement> links
	LogoElement logo
	RightsElement rights
	SubtitleElement subtitle
	TitleElement title
	UpdatedElement updated
	List<EntryElement> entries

	FeedElement() {}

	FeedElement(List<AuthorElement> authors, List<CategoryElement> categories,
			List<ContributorElement> contributors, GeneratorElement generator,
			IconElement icon, IdElement id, List<LinkElement> links, LogoElement logo,
			RightsElement rights, SubtitleElement subtitle, TitleElement title,
			UpdatedElement updated, List<EntryElement> entries) {
		this.authors = authors
		this.categories = categories
		this.contributors = contributors
		this.generator = generator
		this.icon = icon
		this.id = id
		this.links = links
		this.logo = logo
		this.rights = rights
		this.subtitle = subtitle
		this.title = title
		this.updated = updated
		this.entries = entries
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachXmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachAuthors(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachCategories(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachContributors(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachGenerator(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachIcon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachId(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachLinks(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachLogo(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachRights(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachSubtitle(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachTitle(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachUpdated(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachEntries(args, gbd, ssd, rqd, rpd, te, valueSet)
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

	boolean attachGenerator(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Generator." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, GeneratorElement.class).getType()
			GeneratorElement generator = My.deminion(mnn, listType)
			if(generator != null) {
				this.generator = generator
				return true
			}
			return false
		})
	}

	boolean attachIcon(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Icon." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, IconElement.class).getType()
			IconElement icon = My.deminion(mnn, listType)
			if(icon != null) {
				this.icon = icon
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

	boolean attachLogo(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Logo." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, LogoElement.class).getType()
			LogoElement logo = My.deminion(mnn, listType)
			if(logo != null) {
				this.logo = logo
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

	boolean attachSubtitle(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Subtitle." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, SubtitleElement.class).getType()
			SubtitleElement subtitle = My.deminion(mnn, listType)
			if(subtitle != null) {
				this.subtitle = subtitle
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

	boolean attachEntries(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Entries." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, EntryElement.class).getType()
			List<EntryElement> entries = My.deminion(mnn, listType)
			if(entries != null) {
				this.entries = entries
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		authors != null || categories != null || contributors != null ||
			generator != null || icon != null || id != null || links != null ||
			logo != null || rights != null || subtitle != null || title != null ||
			updated != null || entries != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseAuthors(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseCategories(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseContributors(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseGenerator(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseIcon(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseId(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseLinks(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseLogo(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseRights(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseSubtitle(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseTitle(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseUpdated(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseEntries(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
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

	boolean parseGenerator(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(generator == null) {
			return false
		} else {
			generator.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseIcon(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(icon == null) {
			return false
		} else {
			icon.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
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

	boolean parseLogo(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(logo == null) {
			return false
		} else {
			logo.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
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

	boolean parseSubtitle(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(subtitle == null) {
			return false
		} else {
			subtitle.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
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

	boolean parseEntries(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(entries == null) {
			return false
		} else {
			entries.each { entry ->
				entry.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	public static class Builder extends XmlElementImpl.Builder<FeedElement> {

		private List<AuthorElement> authors
		private List<CategoryElement> categories
		private List<ContributorElement> contributors
		private GeneratorElement generator
		private IconElement icon
		private IdElement id
		private List<LinkElement> links
		private LogoElement logo
		private RightsElement rights
		private SubtitleElement subtitle
		private TitleElement title
		private UpdatedElement updated
		private List<EntryElement> entries

		Builder setAuthors(List<AuthorElement> authors) {
			this.authors = authors
			return this
		}

		Builder setCategories(List<CategoryElement> categories) {
			this.categories = categories
			return this
		}

		Builder setContributors(List<ContributorElement> contributors) {
			this.contributors = contributors
			return this
		}

		Builder setGenerator(GeneratorElement generator) {
			this.generator = generator
			return this
		}

		Builder setIcon(IconElement icon) {
			this.icon = icon
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

		Builder setLogo(LogoElement logo) {
			this.logo = logo
			return this
		}

		Builder setRights(RightsElement rights) {
			this.rights = rights
			return this
		}

		Builder setSubtitle(SubtitleElement subtitle) {
			this.subtitle = subtitle
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

		Builder setEntries(List<EntryElement> entries) {
			this.entries = entries
			return this
		}

		@Override
		FeedElement build() {
			FeedElement obj = super.build()
			obj.authors = authors
			obj.categories = categories
			obj.contributors = contributors
			obj.generator = generator
			obj.icon = icon
			obj.id = id
			obj.links = links
			obj.logo = logo
			obj.rights = rights
			obj.subtitle = subtitle
			obj.title = title
			obj.updated = updated
			obj.entries = entries
			return obj
		}

	}

}
