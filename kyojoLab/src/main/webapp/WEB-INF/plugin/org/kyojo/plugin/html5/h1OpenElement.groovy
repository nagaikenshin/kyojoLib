package org.kyojo.plugin.html5

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData

class H1OpenElement extends H1Element {

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

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<H1OpenElement> {
	}

}
