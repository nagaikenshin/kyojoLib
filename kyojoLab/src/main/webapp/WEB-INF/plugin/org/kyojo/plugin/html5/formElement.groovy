package org.kyojo.plugin.html5

import org.apache.commons.lang3.StringUtils
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine

class FormElement extends AnyHtmlElement {

	String label

	protected String extractFormID(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		String iKey = null
		if(attrs.containsKey("id")) {
			if(!attrs.containsKey("name")) {
				attrs.name = attrs.id
			}
			iKey = attrs.id
		}
		if(attrs.containsKey("disabled")) {
			if(StringUtils.isNotBlank(attrs.disabled) && attrs.disabled != "disabled") {
				if(!attrs.containsKey("id")) {
					attrs.id = "_" + attrs.disabled
				}
				if(!attrs.containsKey("name")) {
					attrs.name = "_" + attrs.disabled
				}
				iKey = attrs.disabled
				attrs.disabled = "disabled"
			}
		} else if(attrs.containsKey("readonly")) {
			if(StringUtils.isNotBlank(attrs.readonly) && attrs.readonly != "readonly") {
				if(!attrs.containsKey("id")) {
					attrs.id = "_" + attrs.readonly
				}
				if(!attrs.containsKey("name")) {
					attrs.name = "_" + attrs.readonly
				}
				iKey = attrs.readonly
				attrs.readonly = "readonly"
			}
		}

		return iKey
	}

}
