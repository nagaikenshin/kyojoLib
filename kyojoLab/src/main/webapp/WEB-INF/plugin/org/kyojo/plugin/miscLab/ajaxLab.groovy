package org.kyojo.plugin.miscLab

import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.plugin.miscLab.ajaxLab.TravelItem

class AjaxLab {

	List<TravelItem> travelItemList

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		travelItemList = [
				new TravelItem("Handkerchief"),
				new TravelItem("Facial tissue"),
				new TravelItem("Wallet"),
				new TravelItem("Smartphone"),
				new TravelItem("Rain gear")
			]

		return null
	}

}
