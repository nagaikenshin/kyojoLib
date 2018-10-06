package org.kyojo.plugin.html5

import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine

class ButtonElement extends FormElement {

	void extract(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		extractFormID(args, gbd, ssd, rqd, rpd, te, valueSet)

		composeClass()
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<ButtonElement> {
	}

}
