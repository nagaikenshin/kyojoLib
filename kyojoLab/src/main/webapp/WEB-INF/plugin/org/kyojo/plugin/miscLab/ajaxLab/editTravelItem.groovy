package org.kyojo.plugin.miscLab.ajaxLab

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.minion.My
import org.kyojo.plugin.miscLab.AjaxLab

class EditTravelItem {

	Integer listNo
	Boolean checked

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		te.changeExt("json") // Ajax用

		// AjaxLabのListを本体データとして更新
		def ajaxLabAct = "miscLab/ajaxLab"
		AjaxLab ajaxLab = ssd.takeOver(AjaxLab.class, ajaxLabAct, ajaxLabAct)
		if(ajaxLab != null && ajaxLab.travelItemList != null) {
			if(listNo != null) {
				if(0 < listNo && listNo <= ajaxLab.travelItemList.size()) {
					TravelItem targetItem = ajaxLab.travelItemList[listNo - 1]
					targetItem.checked = checked
					ssd.turnOver(ajaxLab, ajaxLabAct, ajaxLabAct)
					cache.addLine(My.minion([ status: "OK" ]))
				}
			}
		}

		return null
	}

}
