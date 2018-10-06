package org.kyojo.plugin.html5

import java.util.List

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData
import org.kyojo.plugin.html5.SelectElement.Builder

class SelectOpenElement extends SelectElement {

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

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<SelectOpenElement> {

		private List<OptGroupElement> optGroups
		private List<OptionElement> options

		Builder setOptGroups(List<OptGroupElement> optGroups) {
			this.optGroups = optGroups
			return this
		}

		Builder setOptions(List<OptionElement> options) {
			this.options = options
			return this
		}

		@Override
		SelectOpenElement build() {
			SelectOpenElement obj = super.build()
			obj.optGroups = optGroups
			obj.options = options
			return obj
		}

	}

}
