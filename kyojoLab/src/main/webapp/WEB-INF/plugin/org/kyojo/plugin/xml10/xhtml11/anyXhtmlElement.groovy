package org.kyojo.plugin.xml10.xhtml11

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData

abstract class AnyXhtmlElement extends XhtmlElementImpl {

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
	public String getDefaultSpec() {
		return "xml10/xhtml11";
	}

	@OutOfRequestData
	@OutOfResponseData
	String getSpec() {
		getDefaultSpec()
	}

	@OutOfRequestData
	@OutOfResponseData
	void setSpec(String spec) {
	}

}
