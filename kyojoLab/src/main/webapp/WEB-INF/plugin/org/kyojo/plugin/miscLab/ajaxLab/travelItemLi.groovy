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
import org.kyojo.core.annotation.ArgsListNo
import org.kyojo.plugin.miscLab.AjaxLab

class TravelItemLi {

	@ArgsListNo
	Integer listNo
	String name
	Boolean checked

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		te.changeExt("html") // Ajax用

		// AjaxLabのListを本体データとして更新
		def ajaxLabAct = "miscLab/ajaxLab"
		AjaxLab ajaxLab = ssd.takeOver(AjaxLab.class, ajaxLabAct, ajaxLabAct)
		if(ajaxLab != null && ajaxLab.travelItemList != null) {
			if(listNo != null) {
				if(listNo == ajaxLab.travelItemList.size() + 1) {
					// 新しい要素の追加
					ajaxLab.travelItemList.add(this)
					ssd.turnOver(ajaxLab, ajaxLabAct, ajaxLabAct)
				}
			}
		}

		return null
	}

}
