package org.kyojo.plugin.html5

import org.apache.commons.lang3.StringUtils
import org.kyojo.core.Cache
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData

class InputElement extends FormElement {

	@Override
	public String getDefaultName() {
		"input"
	}

	@OutOfRequestData
	@OutOfResponseData
	String getName() {
		getDefaultName()
	}

	@Override
	public String getDefaultType() {
		"text"
	}

	void extract(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		String iKey = extractFormID(args, gbd, ssd, rqd, rpd, te, valueSet)

		if(iKey != null) {
			extractValue(iKey, args, gbd, ssd, rqd, rpd, te, valueSet)
		}

		if(!attrs.containsKey("value")) {
			if(valueSet != null && valueSet.size() > 0) {
				valueSet.each { value ->
					attrs.value = value
					return true
				}
			}
		}

		if(!attrs.containsKey("type")) {
			attrs.type = this.type
		}

		composeClass()
	}

}
