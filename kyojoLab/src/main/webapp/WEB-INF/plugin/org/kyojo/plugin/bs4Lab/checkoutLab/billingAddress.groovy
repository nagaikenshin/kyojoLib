package org.kyojo.plugin.bs4Lab.checkoutLab

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine

class BillingAddress {

	String firstName
	String lastName
	String username
	String email
	String address
	String address2
	String country
	String state
	String zip
	String sameAddress
	String saveInfo
	String paymentMethod
	String ccName
	String ccNumber
	String ccExpiration
	String ccCvv

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		paymentMethod = "credit"

		return null
	}

	Object doCheckout(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {

		return "/bs4Lab/checkoutResult.html"
	}

}
