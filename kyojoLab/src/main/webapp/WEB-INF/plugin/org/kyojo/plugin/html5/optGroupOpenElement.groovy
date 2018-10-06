package org.kyojo.plugin.html5

import java.util.List

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData
import org.kyojo.plugin.html5.OptGroupElement.Builder

class OptGroupOpenElement extends OptGroupElement {

	@OutOfRequestData
	@OutOfResponseData
	public String getOpen() {
		getDefaultOpen()
	}

	@OutOfRequestData
	@OutOfResponseData
	public void setOpen(String open) {
	}

	@Override
	public String getDefaultOpen() {
		"open"
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<OptGroupOpenElement> {

		private List<OptionElement> options

		Builder setOptions(List<OptionElement> options) {
			this.options = options
			return this
		}

		@Override
		OptGroupOpenElement build() {
			OptGroupOpenElement obj = super.build()
			obj.options = options
			return obj
		}

	}

}
