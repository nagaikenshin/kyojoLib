package org.kyojo.plugin.schemaOrgLab

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
import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.schemaorg.SimpleJsonBuilder
import org.kyojo.schemaorg.m3n4.core.Clazz.BreadcrumbList
import org.kyojo.schemaorg.m3n4.core.Clazz.ListItem
import org.kyojo.schemaorg.m3n4.core.Container.ItemListElement
import org.kyojo.schemaorg.m3n4.core.impl.BREADCRUMB
import org.kyojo.schemaorg.m3n4.core.impl.BREADCRUMB_LIST
import org.kyojo.schemaorg.m3n4.core.impl.IDENTIFIER
import org.kyojo.schemaorg.m3n4.core.impl.ITEM
import org.kyojo.schemaorg.m3n4.core.impl.ITEM_LIST_ELEMENT
import org.kyojo.schemaorg.m3n4.core.impl.LIST_ITEM
import org.kyojo.schemaorg.m3n4.core.impl.NAME
import org.kyojo.schemaorg.m3n4.core.impl.POSITION
import org.kyojo.schemaorg.m3n4.core.impl.THING
import org.kyojo.schemaorg.m3n4.core.impl.URL

class BreadcrumbScript {

	String act
	String ext
	@OutOfRequestData
	String breadcrumbJsonLd

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		def baseURI = gbd.get("BASE_URI").toString()
		List<ListItem> listItemList = [
				new LIST_ITEM(
						position: new POSITION(1L),
						name: new NAME("Home"),
						item: new ITEM(new THING(new IDENTIFIER(new URL(baseURI + "/index.html"))))
					)
			]

		def sb = new StringBuilder(baseURI)
		if(StringUtils.isNotBlank(act)) {
			String[] elems = act.split("/")
			elems.eachWithIndex { elem, i ->
				sb << "/" << elem
				def uri = new StringBuilder(sb.toString())
				if(i == elems.length - 1) {
					uri << ".html"
				} else {
					uri << "/index.html"
				}
				listItemList.add(
						new LIST_ITEM(
								position: new POSITION(i + 1L),
								name: new NAME(elem),
								item: new ITEM(new THING(new IDENTIFIER(new URL(uri.toString()))))
							)
					)
			}
		}

		ItemListElement itemListElement = new ITEM_LIST_ELEMENT()
		itemListElement.setListItemList(listItemList)
		BreadcrumbList breadcrumbList = new BREADCRUMB_LIST(itemListElement)
		breadcrumbJsonLd = SimpleJsonBuilder.toJsonLd(breadcrumbList, BreadcrumbList.class)

		return null
	}

}
