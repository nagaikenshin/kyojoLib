package org.kyojo.plugin.html5

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

}
