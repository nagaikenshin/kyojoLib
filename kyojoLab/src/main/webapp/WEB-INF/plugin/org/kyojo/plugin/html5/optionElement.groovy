package org.kyojo.plugin.html5

class OptionElement extends FormElement {

	OptionElement() {
	}

	OptionElement(String value) {
		this.text = value
	}

	OptionElement(Integer value) {
		this("" + value)
	}

	OptionElement(Long value) {
		this("" + value)
	}

	OptionElement(String value, String text) {
		this.attrs = [ value : value ]
		this.text = text
	}

	OptionElement(Integer value, String text) {
		this("" + value, text)
	}

	OptionElement(Long value, String text) {
		this("" + value, text)
	}

	OptionElement(String value, Integer text) {
		this(value, "" + text)
	}

	OptionElement(Integer value, Integer text) {
		this("" + value, "" + text)
	}

	OptionElement(Long value, Integer text) {
		this("" + value, "" + text)
	}

	OptionElement(Map<String, String> attrs, String text) {
		this.attrs = attrs
		this.text = text
	}

	OptionElement(Map<String, String> attrs, Integer text) {
		this(attrs, "" + text)
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<OptionElement> {
	}

}
