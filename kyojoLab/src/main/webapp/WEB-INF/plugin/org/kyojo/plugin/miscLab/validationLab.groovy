package org.kyojo.plugin.miscLab

import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Past
import javax.validation.constraints.Size

import org.apache.commons.lang3.StringUtils

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.core.annotation.InheritDefault
import org.kyojo.plugin.miscLab.validationLab.Person

class ValidationLab {

	@InheritDefault
	String act

	@NotBlank
	@Email
	String email

	@Min(-10L)
	Integer ival

	@NotNull
	@Past
	Date past

	@NotNull
	@Size(min=1, max=1000)
	@Valid
	List<Person> personList;

	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		def person01 = new Person("Alex", 10)
		personList = [ person01 ]

		return null
	}

	Object doSubmit(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(!rpd.hasValidationMessage()) {
			def rdctTo = "miscLab/validationResult"
			ssd.turnOver(this, rdctTo, rdctTo)
			return rdctTo + ".html"
		}

		return null
	}

}
