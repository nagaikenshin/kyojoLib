package org.kyojo.plugin.miscLab.validationLab

import javax.validation.constraints.Email
import javax.validation.constraints.Min
import javax.validation.constraints.Size
import org.kyojo.plugin.html5.HtmlNode
import org.kyojo.plugin.html5.TdElement
import org.kyojo.schemaorg.JsonListNo

class Person {

	@JsonListNo
	int listNo

	@Size(min=1, max=50)
	String name;

	@Min(0L)
	Integer age

	Person() {
	}

	Person(String name, Integer age) {
		this.name = name;
		this.age = age;
	}

	List<HtmlNode> getNodes() {
		[
			new TdElement.Builder().setText("" + listNo).build(),
			new TdElement.Builder().setText(name).build(),
			new TdElement.Builder().setText(age == null ? "" : "" + age).build()
		]
	}

	void setNodes(List<HtmlNode> nodes) {
	}

}
