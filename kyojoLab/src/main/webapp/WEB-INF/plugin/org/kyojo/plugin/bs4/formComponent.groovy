package org.kyojo.plugin.bs4

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

abstract class FormComponent extends org.kyojo.plugin.html5.FormComponent {

	HtmlElement ariaDescribedBy
	HtmlElement valid
	HtmlElement invalid
	HtmlElement validFeedback
	HtmlElement invalidFeedback
	HtmlElement validTooltip
	HtmlElement invalidTooltip

	void giveDescDefaults(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te) throws PluginException {
		String vldMsg = giveValidateMessage(args, gbd, ssd, rqd, rpd, te)
		if(vldMsg != null && invalidFeedback == null) {
			invalidFeedback = new DivElement.Builder().setText(vldMsg).build()
		}

		if(attrs != null && attrs.containsKey("id") && StringUtils.isNotBlank(attrs.id)) {
			if(ariaDescribedBy != null) {
				desc = ariaDescribedBy
				ariaDescribedBy = null
				if(StringUtils.isBlank(desc.name) || desc.name == desc.rootName) {
					desc.name = "small"
				}
				if(desc.attrs == null) {
					desc.attrs = [:]
				}
				desc.attrs.id = attrs.id + "Help"
				desc.attrs["class"] = "form-text text-muted"
				attrs["aria-describedby"] = desc.attrs.id
			}

			if(validFeedback != null) {
				valid = validFeedback
				validFeedback = null
				if(StringUtils.isBlank(valid.name) || valid.name == valid.rootName) {
					valid.name = "div"
				}
				if(valid.attrs == null) {
					valid.attrs = [:]
				}
				valid.attrs["class"] = "valid-feedback"
			} else if(validTooltip != null) {
				valid = validTooltip
				validTooltip = null
				if(StringUtils.isBlank(valid.name) || valid.name == valid.rootName) {
					valid.name = "div"
				}
				if(valid.attrs == null) {
					valid.attrs = [:]
				}
				valid.attrs["class"] = "valid-tooltip"
			}

			if(invalidFeedback != null) {
				invalid = invalidFeedback
				invalidFeedback = null
				if(StringUtils.isBlank(invalid.name) || invalid.name == invalid.rootName) {
					invalid.name = "div"
				}
				if(invalid.attrs == null) {
					invalid.attrs = [:]
				}
				invalid.attrs["class"] = "invalid-feedback"
			} else if(invalidTooltip != null) {
				invalid = invalidTooltip
				invalidTooltip = null
				if(StringUtils.isBlank(invalid.name) || invalid.name == invalid.rootName) {
					invalid.name = "div"
				}
				if(invalid.attrs == null) {
					invalid.attrs = [:]
				}
				invalid.attrs["class"] = "invalid-tooltip"
			}
		}

		if(desc != null) {
			if(StringUtils.isBlank(desc.name) || desc.name == desc.rootName) {
				desc.name = "small"
			}
		}

		return null
	}

}
