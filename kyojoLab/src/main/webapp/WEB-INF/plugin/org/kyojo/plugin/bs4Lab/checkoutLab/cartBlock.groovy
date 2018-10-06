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
import org.kyojo.core.annotation.OutOfRequestData

class CartBlock {

	@OutOfRequestData
	List<CartItem> cartList
	@OutOfRequestData
	Integer cartProductNum
	@OutOfRequestData
	Integer totalAmount
	String promoCode

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		cartList = []
		cartList.add(new CartItem("P", "Product name", "Brief description", 12))
		cartList.add(new CartItem("P", "Second product", "Brief description", 8))
		cartList.add(new CartItem("P", "Third item", "Brief description", 5))
		cartList.add(new CartItem("V", "Promo code", "EXAMPLECODE", -5))
		updateCartInfo()

		return null
	}

	void updateCartInfo() {
		totalAmount = 0
		cartProductNum = 0

		if(cartList == null) {
			cartList = []
		}
		cartList.each {
			if(it.productType == "P") {
				cartProductNum++
			}
			totalAmount += it.productAmount
		}
	}

	Object doRedeem(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		cartList.add(new CartItem("V", "Promo code", promoCode, -5))
		promoCode = ""
		updateCartInfo()

		return null
	}

}
