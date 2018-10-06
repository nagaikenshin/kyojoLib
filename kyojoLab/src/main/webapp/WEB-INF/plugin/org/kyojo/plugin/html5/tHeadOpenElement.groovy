package org.kyojo.plugin.html5

import java.util.List

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData
import org.kyojo.plugin.html5.THeadElement.Builder

class THeadOpenElement extends THeadElement {

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

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<THeadOpenElement> {

		private List<TrElement> trs

		Builder setTrs(List<TrElement> trs) {
			this.trs = trs
			return this
		}

		@Override
		THeadOpenElement build() {
			THeadOpenElement obj = super.build()
			obj.trs = trs
			return obj
		}

	}

}
