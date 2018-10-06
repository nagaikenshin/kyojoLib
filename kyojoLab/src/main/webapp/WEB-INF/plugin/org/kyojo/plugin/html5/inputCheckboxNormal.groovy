package org.kyojo.plugin.html5

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
		return new LiElement()
	}

}
