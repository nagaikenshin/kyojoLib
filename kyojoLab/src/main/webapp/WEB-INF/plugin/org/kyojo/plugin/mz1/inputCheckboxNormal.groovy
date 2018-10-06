package org.kyojo.plugin.mz1

import org.apache.commons.lang3.StringUtils

import org.kyojo.plugin.html5.LabelElement
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.plugin.html5.HtmlElement

class InputCheckboxNormal extends FormComponent {

	Boolean checked

	@Override
	protected HtmlElement createFormElement() {
		InputCheckboxElement form = new InputCheckboxElement()
		if(checked != null) {
			form.checked = checked
		}
		return form
	}

	@Override
	void giveLabelDefaults(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te) throws PluginException {
		if(label != null) {
			if(StringUtils.isBlank(label.name) || label.name == label.rootName) {
				label.name = "span"
			}
		}
	}

	@Override
	protected HtmlElement createCompoElement() {
		return new LabelElement()
	}

}
