package org.kyojo.plugin.html5

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
		return new LiElement()
	}

}
