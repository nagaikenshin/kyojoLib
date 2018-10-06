package org.kyojo.plugin.bs4Lab

import org.apache.commons.lang3.StringUtils

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.plugin.html5.AElement
import org.kyojo.plugin.html5.LiElement

class Nav {

	String act
	String ext
	List<LiElement> breadcrumbList

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		def baseURI = gbd.get("BASE_URI").toString()
		breadcrumbList = []
		def breadcrumb = new LiElement(
				attrs: [
						class: "breadcrumb-item"
					],
				nodes: [
						new AElement(
								attrs: [
										href: baseURI + "/index.html"
									],
								text: "Home"
							)
					]
			)
		breadcrumbList.add(breadcrumb)

		def sb = new StringBuilder(baseURI)
		if(StringUtils.isNotBlank(act)) {
			String[] elems = act.split("/")
			elems.eachWithIndex { elem, i ->
				sb << "/" << elem
				breadcrumb = new LiElement()
				if(i == elems.length - 1) {
					breadcrumb.attrs = [
						class: "breadcrumb-item active",
						"aria-current": "page"
					]
					breadcrumb.text = elem
				} else {
					breadcrumb.attrs = [
						class: "breadcrumb-item"
					]
					breadcrumb.nodes = [
							new AElement(
									attrs: [
											href: sb.toString() + "/index.html"
										],
									text: elem
								)
						]
				}
				breadcrumbList.add(breadcrumb)
			}
		}

		return null
	}

}
