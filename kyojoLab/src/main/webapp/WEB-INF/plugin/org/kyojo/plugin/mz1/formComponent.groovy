package org.kyojo.plugin.mz1

import org.apache.commons.lang3.StringUtils

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.plugin.html5.DivElement
import org.kyojo.plugin.html5.HtmlElement
import org.kyojo.plugin.html5.SmallElement
import org.kyojo.plugin.html5.SpanElement

abstract class FormComponent extends org.kyojo.plugin.html5.FormComponent {

	HtmlElement helperText

	void giveDescDefaults(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te) throws PluginException {
		String vldMsg = giveValidateMessage(args, gbd, ssd, rqd, rpd, te)
		if(vldMsg != null) {
			if(helperText == null) {
				helperText = new SpanElement()
			}
			if(helperText.attrs == null) {
				helperText.attrs = [:]
			}
			helperText.attrs["data-error"] = vldMsg

			if(form.addClass == null) {
				form.addClass = "invalid"
			} else {
				form.addClass += " invalid"
			}
		}

		if(helperText != null) {
			desc = helperText
			helperText = null
			if(StringUtils.isBlank(desc.name) || desc.name == desc.rootName) {
				desc.name = "span"
			}
			if(desc.attrs == null) {
				desc.attrs = [:]
			}
			if(attrs != null && attrs.containsKey("id") && StringUtils.isNotBlank(attrs.id)) {
				desc.attrs.id = attrs.id + "Help"
			}
			desc.attrs["class"] = "helper-text"
		}

		if(desc != null) {
			if(StringUtils.isBlank(desc.name) || desc.name == desc.rootName) {
				desc.name = "span"
			}
		}

		return null
	}

}
