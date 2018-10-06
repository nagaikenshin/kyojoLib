package org.kyojo.plugin.xml10.xhtml11

import java.util.List

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData
import org.kyojo.plugin.html5.OlElement.Builder

class OlOpenElement extends OlElement {

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

	public static class Builder extends org.kyojo.plugin.xml10.xhtml11.XhtmlElementImpl.Builder<OlOpenElement> {

		private List<LiElement> lis

		Builder setLis(List<LiElement> lis) {
			this.lis = lis
			return this
		}

		@Override
		OlOpenElement build() {
			OlOpenElement obj = super.build()
			obj.lis = lis
			return obj
		}

	}

}
