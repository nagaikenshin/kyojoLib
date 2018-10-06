package org.kyojo.plugin.html5

import java.util.List

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData
import org.kyojo.plugin.html5.OlOpenElement.Builder

class UlOpenElement extends UlElement {

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

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<UlOpenElement> {

		private List<LiElement> lis

		Builder setLis(List<LiElement> lis) {
			this.lis = lis
			return this
		}

		@Override
		UlOpenElement build() {
			UlOpenElement obj = super.build()
			obj.lis = lis
			return obj
		}

	}

}
