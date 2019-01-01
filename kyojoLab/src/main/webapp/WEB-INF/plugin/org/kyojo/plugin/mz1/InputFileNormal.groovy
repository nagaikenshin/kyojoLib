package org.kyojo.plugin.mz1

import java.util.stream.Stream

import org.apache.commons.lang3.StringUtils

import org.kyojo.plugin.html5.DivElement
import org.kyojo.plugin.html5.HtmlElement

class InputFileNormal extends FormComponent {

	String buttonLabel
	Map<String, String> buttonAttrs
	String buttonDefaultClass
	String buttonAddClass

	@Override
	protected HtmlElement createFormElement() {
		return new InputFileElement()
	}

	@Override
	protected HtmlElement createCompoElement() {
		HtmlElement compo = new DivElement()
		compo.defaultClass = "file-field input-field"

		if(StringUtils.isBlank(buttonDefaultClass)) {
			buttonDefaultClass = "btn"
		}
		if(buttonAttrs == null) {
			buttonAttrs = [ "class": buttonDefaultClass ]
		} else if(!buttonAttrs.containsKey("class")
				|| StringUtils.isBlank(buttonAttrs["class"])) {
			buttonAttrs["class"] = buttonDefaultClass
		}
		if(StringUtils.isNotBlank(buttonAddClass)) {
			Set<String> clss = []
			buttonAttrs["class"].split("\\s+").each { cls ->
				clss.add(cls)
			}
			buttonAddClass.split("\\s+").each { cls ->
				clss.add(cls)
			}
			buttonAttrs["class"] = StringUtils.join(clss, " ")
		}

		return compo
	}

}
