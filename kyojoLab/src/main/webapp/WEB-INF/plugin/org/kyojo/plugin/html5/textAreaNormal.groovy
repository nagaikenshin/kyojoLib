package org.kyojo.plugin.html5

import org.apache.commons.lang3.StringUtils

class TextAreaNormal extends FormComponent {

	String text

	@Override
	protected HtmlElement createFormElement() {
		TextAreaElement form = new TextAreaElement()
		form.text = text

		return form
	}

}
