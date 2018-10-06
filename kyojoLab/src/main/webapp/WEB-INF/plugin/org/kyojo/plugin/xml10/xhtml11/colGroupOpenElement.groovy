package org.kyojo.plugin.xml10.xhtml11

import java.util.List

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData
import org.kyojo.plugin.html5.ColGroupElement.Builder

class ColGroupOpenElement extends ColGroupElement {

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

	public static class Builder extends org.kyojo.plugin.xml10.xhtml11.XhtmlElementImpl.Builder<ColGroupOpenElement> {

		private List<ColElement> cols

		Builder setCols(List<ColElement> cols) {
			this.cols = cols
			return this
		}

		@Override
		ColGroupOpenElement build() {
			ColGroupOpenElement obj = super.build()
			obj.cols = cols
			return obj
		}

	}

}
