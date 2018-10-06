package org.kyojo.plugin.html5

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData

class InputCheckboxElement extends InputGroupElement {

	@Override
	public String getDefaultType() {
		"checkbox"
	}

	@OutOfRequestData
	@OutOfResponseData
	String getType() {
		getDefaultType()
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<InputCheckboxElement> {
	}

}
