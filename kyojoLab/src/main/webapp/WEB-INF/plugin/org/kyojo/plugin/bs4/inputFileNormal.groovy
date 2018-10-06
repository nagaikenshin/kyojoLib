package org.kyojo.plugin.bs4

import org.kyojo.plugin.html5.DivElement
import org.kyojo.plugin.html5.HtmlElement

class InputFileNormal extends FormComponent {

	@Override
	protected HtmlElement createFormElement() {
		return new InputFileElement()
	}

	@Override
	protected HtmlElement createCompoElement() {
		HtmlElement compo = new DivElement()
		compo.defaultClass = "form-group"

		return compo
	}

}
