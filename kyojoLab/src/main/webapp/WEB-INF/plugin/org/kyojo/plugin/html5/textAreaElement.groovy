package org.kyojo.plugin.html5

import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.core.Time14

class TextAreaElement extends FormElement {

	@Override
	protected String getDefaultExpires() {
		return Time14.OLD.toString()
	}

	void extract(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		String iKey = extractFormID(args, gbd, ssd, rqd, rpd, te, valueSet)

		if(iKey != null) {
			extractValue(iKey, args, gbd, ssd, rqd, rpd, te, valueSet)
		}

		if(valueSet != null && valueSet.size() > 0) {
			valueSet.each { value ->
				text = value
				return true
			}
		}

		composeClass()
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<TextAreaElement> {
	}

}
