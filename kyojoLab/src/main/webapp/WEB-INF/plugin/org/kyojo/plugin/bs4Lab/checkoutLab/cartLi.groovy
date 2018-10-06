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

class CartLi {

	String liAddClass
	String productMainAddClass
	String productType
	String productName
	String productDesc
	String productDescAddClass
	Integer productAmount
	String productAmountAddClass

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		switch(productType) {
		case "V":
			liAddClass = "bg-light"
			productMainAddClass = "text-success"
			productDescAddClass = null
			productAmountAddClass = "text-success"
			break
		default:
			liAddClass = "lh-condensed"
			productMainAddClass = null
			productDescAddClass = "text-muted"
			productAmountAddClass = "text-muted"
			break
		}

		return null
	}

}
