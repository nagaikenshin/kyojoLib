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

class ContributorElement extends AnyXmlElement {

	NameElement name_
	UriElement uri
	EmailElement email

	ContributorElement() {}

	ContributorElement(NameElement name, UriElement uri, EmailElement email) {
		this.name_ = name
		this.uri = uri
		this.email = email
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachXmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachName(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachUri(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachEmail(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	boolean attachName(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Name." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, NameElement.class).getType()
			NameElement name = My.deminion(mnn, listType)
			if(name != null) {
				this.name_ = name
				return true
			}
			return false
		})
	}

	boolean attachUri(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Uri." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, UriElement.class).getType()
			UriElement uri = My.deminion(mnn, listType)
			if(uri != null) {
				this.uri = uri
				return true
			}
			return false
		})
	}

	boolean attachEmail(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Email." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, EmailElement.class).getType()
			EmailElement email = My.deminion(mnn, listType)
			if(email != null) {
				this.email = email
				return true
			}
			return false
		})
	}

	@Override
	boolean hasChildren() {
		name_ != null || uri != null || email != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseName(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseUri(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			parseEmail(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) |
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseName(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(name_ == null) {
			return false
		} else {
			name_.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseUri(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(uri == null) {
			return false
		} else {
			uri.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	boolean parseEmail(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(email == null) {
			return false
		} else {
			email.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			return true
		}
	}

	public static class Builder extends XmlElementImpl.Builder<ContributorElement> {

		private NameElement name
		private UriElement uri
		private EmailElement email

		Builder setName(NameElement name) {
			this.name = name
			return this
		}

		Builder setUri(UriElement uri) {
			this.uri = uri
			return this
		}

		Builder setEmail(EmailElement email) {
			this.email = email
			return this
		}

		@Override
		ContributorElement build() {
			ContributorElement obj = super.build()
			obj.name_ = name
			obj.uri = uri
			obj.email = email
			return obj
		}

	}

}
