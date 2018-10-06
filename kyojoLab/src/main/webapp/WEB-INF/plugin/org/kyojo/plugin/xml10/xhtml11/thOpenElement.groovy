package org.kyojo.plugin.xml10.xhtml11

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData

class ThOpenElement extends ThElement {

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

	public static class Builder extends org.kyojo.plugin.xml10.xhtml11.XhtmlElementImpl.Builder<ThOpenElement> {
	}

}
