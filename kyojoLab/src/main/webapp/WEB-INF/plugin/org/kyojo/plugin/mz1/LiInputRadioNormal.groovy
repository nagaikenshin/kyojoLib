package org.kyojo.plugin.mz1

import org.kyojo.plugin.html5.HtmlElement
import org.kyojo.plugin.html5.LabelElement
import org.kyojo.plugin.mz1.InputRadioNormal

class LiInputRadioNormal extends InputRadioNormal {

	@Override
	protected HtmlElement createCompoElement() {
		return new LabelElement.Builder().setDefaultClass("inputRadioNormal").build()
	}

}
