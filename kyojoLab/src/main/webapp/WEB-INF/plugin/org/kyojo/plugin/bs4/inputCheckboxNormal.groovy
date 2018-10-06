package org.kyojo.plugin.bs4

import org.kyojo.plugin.html5.DivElement
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
	protected HtmlElement createCompoElement() {
		HtmlElement compo = new DivElement()
		compo.defaultClass = "form-group form-check"

		return compo
	}

}
