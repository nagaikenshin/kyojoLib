package org.kyojo.plugin.html5

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData

abstract class AnyHtmlElement extends HtmlElementImpl {

	@Override
	public String getDefaultName() {
		getElementName()
	}

	@OutOfRequestData
	@OutOfResponseData
	String getName() {
		getElementName()
	}

	@OutOfRequestData
	@OutOfResponseData
	void setName(String name) {
	}

	@Override
	public String getDefaultType() {
	}

	@OutOfRequestData
	@OutOfResponseData
	String getType() {
	}

	@OutOfRequestData
	@OutOfResponseData
	void setType(String type) {
	}

}
