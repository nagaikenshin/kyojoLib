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
import org.kyojo.plugin.html5.OptGroupElement
import org.kyojo.plugin.html5.OptionElement

class SelectNormal extends FormComponent {

	List<OptGroupElement> optGroups
	List<OptionElement> options
	HtmlElement icon

	@Override
	protected HtmlElement createFormElement() {
		SelectElement form = new SelectElement()
		if(options != null) {
			form.nodes = options
		} else if(optGroups != null) {
			form.nodes = optGroups
		}

		return form
	}

	@Override
	protected HtmlElement createCompoElement() {
		HtmlElement compo = new DivElement()
		compo.defaultClass = "input-field"

		return compo
	}

	void giveIconDefaults() {
		if(icon != null) {
			if(StringUtils.isBlank(icon.name) || icon.name == icon.rootName) {
				icon.name = "i"
			}

			if(StringUtils.isBlank(icon.defaultClass)) {
				icon.defaultClass = "material-icons prefix"
			}
		}
	}

	@Override
	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		super.buildCache(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)

		giveIconDefaults()

		return null
	}

}
