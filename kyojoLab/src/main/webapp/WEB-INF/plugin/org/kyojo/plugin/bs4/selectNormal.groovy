package org.kyojo.plugin.bs4

import org.kyojo.plugin.html5.DivElement
import org.kyojo.plugin.html5.HtmlElement
import org.kyojo.plugin.html5.OptGroupElement
import org.kyojo.plugin.html5.OptionElement

class SelectNormal extends FormComponent {

	List<OptGroupElement> optGroups
	List<OptionElement> options

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
		compo.defaultClass = "form-group"

		return compo
	}

}
