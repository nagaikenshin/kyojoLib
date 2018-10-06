package org.kyojo.plugin.html5

import java.util.List

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData
import org.kyojo.plugin.html5.TrElement.Builder

class TrOpenElement extends TrElement {

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

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<TrOpenElement> {

		private List<TdElement> tds

		Builder setTds(List<TdElement> tds) {
			this.tds = tds
			return this
		}

		@Override
		TrOpenElement build() {
			TrOpenElement obj = super.build()
			obj.tds = tds
			return obj
		}

	}

}
