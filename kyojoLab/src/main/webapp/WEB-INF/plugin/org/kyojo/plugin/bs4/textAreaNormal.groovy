package org.kyojo.plugin.bs4

import org.kyojo.plugin.html5.DivElement
import org.kyojo.plugin.html5.HtmlElement

class TextAreaNormal extends FormComponent {

	String text

	@Override
	protected HtmlElement createFormElement() {
		TextAreaElement form = new TextAreaElement()
		form.text = text

		return form
	}

	@Override
	protected HtmlElement createCompoElement() {
		HtmlElement compo = new DivElement()
		compo.defaultClass = "form-group"

		return compo
	}

}
