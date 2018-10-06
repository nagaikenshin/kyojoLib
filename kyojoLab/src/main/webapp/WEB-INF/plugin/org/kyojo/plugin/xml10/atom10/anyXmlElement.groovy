package org.kyojo.plugin.xml10.atom10

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData

abstract class AnyXmlElement extends org.kyojo.plugin.xml10.AnyXmlElement {

	@Override
	public String getDefaultSpec() {
		return "xml10/atom10";
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
