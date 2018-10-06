package org.kyojo.plugin.xml10.atom10

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData
import org.kyojo.plugin.xml10.XmlElementImpl

class PublishedOpenElement extends PublishedElement {

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

	public static class Builder extends XmlElementImpl.Builder<PublishedOpenElement> {
	}

}
