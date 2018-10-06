package org.kyojo.plugin.html5

import org.kyojo.core.Time14
import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData

class InputTextElement extends InputElement {

	@Override
	public String getDefaultType() {
		"text"
	}

	@OutOfRequestData
	@OutOfResponseData
	String getType() {
		getDefaultType()
	}

	@Override
	protected String getDefaultExpires() {
		return Time14.OLD.toString()
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<InputTextElement> {
	}

}
