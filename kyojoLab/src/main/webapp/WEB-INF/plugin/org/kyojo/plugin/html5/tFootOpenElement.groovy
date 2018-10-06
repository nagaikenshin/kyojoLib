package org.kyojo.plugin.html5

import java.util.List

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData
import org.kyojo.plugin.html5.TFootElement.Builder

class TFootOpenElement extends TFootElement {

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

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<TFootOpenElement> {

		private List<TrElement> trs

		Builder setTrs(List<TrElement> trs) {
			this.trs = trs
			return this
		}

		@Override
		TFootOpenElement build() {
			TFootOpenElement obj = super.build()
			obj.trs = trs
			return obj
		}

	}

}
