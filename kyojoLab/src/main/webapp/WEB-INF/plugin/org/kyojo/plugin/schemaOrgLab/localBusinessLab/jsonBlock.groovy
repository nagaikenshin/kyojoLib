package org.kyojo.plugin.schemaOrgLab.localBusinessLab

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.minion.My
import org.kyojo.plugin.html5.AElement
import org.kyojo.plugin.html5.DivElement
import org.kyojo.plugin.html5.HtmlElement
import org.kyojo.plugin.html5.HtmlNode
import org.kyojo.plugin.html5.HtmlNodeImpl
import org.kyojo.plugin.html5.LiElement
import org.kyojo.plugin.html5.UlElement
import org.kyojo.schemaorg.SchemaOrgLabel
import org.kyojo.schemaorg.SchemaOrgType
import org.kyojo.schemaorg.SchemaOrgURI
import org.kyojo.schemaorg.SimpleJsonBuilder
import org.kyojo.schemaorg.SimpleJsonWalker
import org.kyojo.schemaorg.SimpleJsonWalker.JsonLdAtIdStringGiven
import org.kyojo.schemaorg.SimpleJsonWalker.JsonLdThingStringGiven
import org.kyojo.schemaorg.m3n4.core.Clazz.ListItem
import org.kyojo.schemaorg.m3n4.core.Container.Item
import org.kyojo.schemaorg.m3n4.core.DataType

class JsonBlock {

	private static final Log logger = LogFactory.getLog(JsonBlock.class)

	String jsonLd
	HtmlNode jsonRoot

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		jsonRoot = null

		if(StringUtils.isBlank(jsonLd)) {
		} else if(jsonLd.length() > 50000) {
		} else {
			logger.info(jsonLd);
			Map<String, String> jsonLdRootMap = new HashMap<>()
			Map<String, JsonLdThingStringGiven> thingStrModeMap = null
			thingStrModeMap = new HashMap<>()
			thingStrModeMap.put("url", JsonLdThingStringGiven.AS_URL)
			thingStrModeMap.put("image", JsonLdThingStringGiven.AS_URL)
			thingStrModeMap.put("item", JsonLdThingStringGiven.AS_THING_IDENTIFIER_URL)
			thingStrModeMap.put("dayOfWeek", JsonLdThingStringGiven.AS_INHERIT)
			thingStrModeMap.put("actionPlatform", JsonLdThingStringGiven.AS_URL)
			String json = SimpleJsonWalker.jsonLdToJson("{\"item\":" + jsonLd + "}", jsonLdRootMap,
					thingStrModeMap, JsonLdAtIdStringGiven.AS_AUTO)
			logger.info(json);
			ListItem obj = My.deminion(json, ListItem.class)
			UlElement rootItemUl = jsonObjectToHtmlUl(obj.item, Item.class)
			if(rootItemUl != null && rootItemUl.nodes.size() > 0
					&& rootItemUl.nodes[0].nodes.size() > 1) {
				jsonRoot = rootItemUl.nodes[0]
				((HtmlElement)jsonRoot.nodes[0]).setAttrs([ class: "schemaOrgJsonRootNameDiv" ])
				((HtmlElement)jsonRoot.nodes[1]).setAttrs([ class: "schemaOrgJsonRootValueUl" ])
			}
		}

		return null
	}

	private <T extends SchemaOrgType> UlElement jsonObjectToHtmlUl(Object obj, Class<T> ifcCls) {
		if(obj == null) return null

		String valStr = null
		if(DataType.Text.class.isAssignableFrom(ifcCls)) {
			DataType.Text val = (DataType.Text)obj
			valStr = val.getString()
		} else if(DataType.Boolean.class.isAssignableFrom(ifcCls)) {
			DataType.Boolean val = (DataType.Boolean)obj
			valStr = val.getB00lean().toString()
		} else if(DataType.DateTime.class.isAssignableFrom(ifcCls)) {
			DataType.DateTime val = (DataType.DateTime)obj
			OffsetDateTime odt = val.getDateTime()
			if(odt != null) {
				DateTimeFormatter ymdhmszDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
				valStr = odt.format(ymdhmszDtf)
			}
		} else if(DataType.Date.class.isAssignableFrom(ifcCls)) {
			DataType.Date val = (DataType.Date)obj
			LocalDate ld = val.getDate()
			if(ld != null) {
				DateTimeFormatter ymdDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd")
				valStr = ld.format(ymdDtf)
			}
		} else if(DataType.Time.class.isAssignableFrom(ifcCls)) {
			DataType.Time val = (DataType.Time)obj
			LocalTime lt = val.getTime()
			if(lt != null) {
				DateTimeFormatter hmsDtf = DateTimeFormatter.ofPattern("HH:mm:ss")
				valStr = lt.format(hmsDtf)
			}
		} else if(DataType.Number.class.isAssignableFrom(ifcCls)) {
			DataType.Number val = (DataType.Number)obj
			valStr = val.getNumber().toString()
		}
		if(valStr != null) {
			DivElement valueDiv = new DivElement.Builder().setText(valStr).build()
			valueDiv.setAttrs([ class: "schemaOrgJsonValueDiv" ])
			LiElement valueLi = new LiElement.Builder().setNodes([ valueDiv ]).build()
			valueLi.setAttrs([ class: "schemaOrgJsonValueLi" ])

			return new UlElement.Builder().setAttrs([ class: "schemaOrgJsonObjectUl" ])
					.setNodes([valueLi]).build()
		}

		Class<?> implCls = obj.getClass()
		List<LiElement> cldLis = []
		List<Field> flds = SimpleJsonBuilder.getAllFields(implCls)
		for(Field fld : flds) {
			fld.setAccessible(true)
			Object val = fld.get(obj)
			DivElement cldValueDiv
			if(val == null) {
				continue
			} else if(List.class.isAssignableFrom(fld.getType())) {
				ParameterizedType gType = (ParameterizedType)fld.getGenericType()
				Type[] aTypes = gType.getActualTypeArguments()
				if(aTypes.length > 0 && aTypes[0] instanceof Class) {
					Class<?> cldCls = (Class<?>)aTypes[0]
					if(SchemaOrgType.class.isAssignableFrom(cldCls)) {
						List<HtmlNode> gcldLis = []
						int gci = 0
						DivElement gcldValueDiv
						LiElement gcldLi
						for(Object gcld : (List<?>)val) {
							gci++

							UlElement gcldValueUl = jsonObjectToHtmlUl(gcld, cldCls)
							if(gcldValueUl != null) {
								gcldValueUl.setAttrs([ class: "schemaOrgJsonItemValueUl" ])
							}
							DivElement gcldNameDiv = new DivElement.Builder().setText("" + gci).build()
							gcldNameDiv.setAttrs([ class: "schemaOrgJsonItemNameDiv" ])
							gcldLi = new LiElement.Builder().setNodes([ gcldNameDiv, gcldValueUl ]).build()
							gcldLi.setAttrs([ class: "schemaOrgJsonItemLi" ])
							gcldLis.add(gcldLi)
						}
						if(gcldLis.size() == 1) {
							HtmlElement cldValueElement = gcldLis[0].nodes[1]
							if(cldValueElement != null) {
								cldValueElement.setAttrs([ class: "schemaOrgJsonOneItemValueUl" ])
							}
							DivElement cldNameDiv = getNameDiv(cldCls)
							cldNameDiv.setAttrs([ class: "schemaOrgJsonOneItemNameDiv" ])
							LiElement cldLi = new LiElement.Builder().setNodes([ cldNameDiv, cldValueElement ]).build()
							cldLi.setAttrs([ class: "schemaOrgJsonOneItemLi" ])
							cldLis.add(cldLi)
						} else if(gcldLis.size() > 1) {
							UlElement cldValueUl = new UlElement.Builder().setNodes(gcldLis).build()
							cldValueUl.setAttrs([ class: "schemaOrgJsonListValueUl" ])
							DivElement cldNameDiv = getNameDiv(cldCls)
							cldNameDiv.setAttrs([ class: "schemaOrgJsonListNameDiv" ])
							LiElement cldLi = new LiElement.Builder().setNodes([ cldNameDiv, cldValueUl ]).build()
							cldLi.setAttrs([ class: "schemaOrgJsonListLi" ])
							cldLis.add(cldLi)
						}
					}
				}
			} else if(SchemaOrgType.class.isAssignableFrom(fld.getType())) {
				UlElement cldValueUl = jsonObjectToHtmlUl(val, fld.getType())
				cldValueUl.setAttrs([ class: "schemaOrgJsonObjectValueUl" ])
				DivElement cldNameDiv = getNameDiv(fld.getType())
				cldNameDiv.setAttrs([ class: "schemaOrgJsonObjectNameDiv" ])
				LiElement cldLi = new LiElement.Builder().setNodes([ cldNameDiv, cldValueUl ]).build()
				cldLi.setAttrs([ class: "schemaOrgJsonObjectLi" ])
				cldLis.add(cldLi)
			}
		}

		if(cldLis.size() == 0) {
			return null
		} else {
			return new UlElement.Builder().setAttrs([ class: "schemaOrgJsonObjectUl" ])
				.setNodes(cldLis).build()
		}
	}

	private DivElement getNameDiv(Class<? extends SchemaOrgType> cls) {
		String name = cls.getSimpleName()
		SchemaOrgLabel schemaOrgLabel = cls.getAnnotation(SchemaOrgLabel.class)
		if(schemaOrgLabel != null) {
			name = schemaOrgLabel.value()
		}
		SchemaOrgURI schemaOrgURI = cls.getAnnotation(SchemaOrgURI.class)

		DivElement nameDiv = new DivElement()
		if(schemaOrgURI == null) {
			nameDiv.setText(name)
		} else {
			AElement nameLink = new AElement.Builder().setAttrs([
				href: schemaOrgURI.value()
			]).setText(name).build()
			nameDiv.setNodes([ nameLink ])
		}

		return nameDiv
	}

}
