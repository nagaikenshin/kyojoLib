package org.kyojo.plugin.html5Lab

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.core.Time14
import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.plugin.html5.DivElement
import org.kyojo.plugin.html5.GenericHtmlElement
import org.kyojo.plugin.html5.GenericHtmlNode
import org.kyojo.plugin.html5.HtmlElement
import org.kyojo.plugin.html5.HtmlNode
import org.kyojo.plugin.html5.InputTextElement
import org.kyojo.plugin.html5.OptGroupElement
import org.kyojo.plugin.html5.OptionElement

class GenericHtmlLab {

	HtmlElement div1
	String span3Class
	String ri

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		InputTextElement node7 = new InputTextElement()
		node7.attrs = [:]
		node7.attrs.id = "ri"
		node7.attrs.name = "ri"
		HtmlNode node6 = new GenericHtmlNode()
		node6.text = "離"
		HtmlElement p5 = new GenericHtmlElement("p")
		p5.attrs = [ style: "padding:5px;border:solid green 1px;" ]
		p5.text = "破"
		HtmlNode node4 = new GenericHtmlNode()
		node4.nodes = [ p5, node6, node7 ]
		HtmlElement span3 = new GenericHtmlElement("span")
		span3.attrs = [ id: "span3" ]
		span3.text = "守"
		span3Class = "red"
		DivElement div2 = new DivElement()
		div2.nodes = [ span3, node4 ]
		// div2.nodes = [ span3 ]
		div1 = new GenericHtmlElement("div")
		div1.text = "HTML JSON expresses"
		div1.nodes = [ div2 ]

		return null
	}

	Object doSubmit(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		GenericHtmlNode.sanitize(div1)

		return null
	}

}
