package org.kyojo.plugin.mz1

import org.kyojo.plugin.html5.HtmlElement
import org.kyojo.plugin.html5.LabelElement
import org.kyojo.plugin.mz1.InputCheckboxNormal

class LiInputCheckboxNormal extends InputCheckboxNormal {

	@Override
	protected HtmlElement createCompoElement() {
		return new LabelElement.Builder().setDefaultClass("inputCheckboxNormal").build()
	}

}
