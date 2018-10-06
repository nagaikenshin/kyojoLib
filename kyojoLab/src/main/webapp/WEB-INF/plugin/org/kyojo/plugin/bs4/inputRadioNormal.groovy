package org.kyojo.plugin.bs4

import org.kyojo.plugin.html5.DivElement
import org.kyojo.plugin.html5.HtmlElement

class InputRadioNormal extends FormComponent {

	Boolean checked

	@Override
	protected HtmlElement createFormElement() {
		InputRadioElement form = new InputRadioElement()
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
