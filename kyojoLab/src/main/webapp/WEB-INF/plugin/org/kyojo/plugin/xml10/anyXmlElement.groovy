package org.kyojo.plugin.xml10

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData

abstract class AnyXmlElement extends XmlElementImpl {

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

}
