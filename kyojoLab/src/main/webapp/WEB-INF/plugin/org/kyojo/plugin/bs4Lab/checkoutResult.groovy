package org.kyojo.plugin.bs4Lab

import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.plugin.bs4Lab.checkoutLab.BillingAddress
import org.kyojo.plugin.bs4Lab.checkoutLab.CartBlock

class CheckoutResult {

	CartBlock cartBlock
	BillingAddress billingAddress

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		recycle(args, gbd, ssd, rqd, rpd)
	}

	Object recycle(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		cartBlock = ssd.takeOver(CartBlock.class, "bs4Lab/checkoutLab/cartBlock", "bs4Lab/checkoutLab")
		billingAddress = ssd.takeOver(BillingAddress.class, "bs4Lab/checkoutLab/billingAddress", "bs4Lab/checkoutLab")

		return null
	}

}
