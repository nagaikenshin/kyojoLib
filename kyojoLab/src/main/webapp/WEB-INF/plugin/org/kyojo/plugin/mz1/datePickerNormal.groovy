package org.kyojo.plugin.mz1

import org.kyojo.plugin.html5.HtmlElement

class DatePickerNormal extends InputTextNormal {

	@Override
	protected HtmlElement createFormElement() {
		return new DatePickerElement()
	}

}
