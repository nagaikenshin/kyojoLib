package org.kyojo.plugin.cmn

import java.lang.annotation.AnnotationFormatError
import java.lang.reflect.Type

import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.StringEscapeUtils
import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.gson.reflect.TypeToken
import org.kyojo.minion.My
import org.kyojo.schemaorg.SimpleJsonBuilder
import org.kyojo.schemaorg.m3n3.core.Clazz
import org.kyojo.schemaorg.m3n3.core.DataType
import org.kyojo.schemaorg.m3n3.core.Container.Gender
import org.kyojo.schemaorg.m3n3.core.Container.Name

abstract class ThingLister {

	abstract String getName()

	abstract void addParentHtmlLines(Cache cache)

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		Map<String, List<Object>> map = null
		if(StringUtils.isBlank(args)) {
			map = new HashMap<>()
		} else {
			Type listType = TypeToken.getParameterized(List.class, Object.class).getType()
			Type mapType = TypeToken.getParameterized(Map.class, String.class, listType).getType()
			map = My.deminion(args, mapType)
		}
		if(map == null || map.size() != 1) {
			// cache.addLine("\n")
			return null
		}
		String key = null
		List<Object> list = null
		for(Map.Entry<String, List<Object>> ent : map) {
			key = ent.getKey()
			list = ent.getValue()
		}
		if(key == null) {
			// cache.addLine("\n")
			return null
		}

		addParentHtmlLines(cache)

		String mnn = "[]";
		if(list != null) {
			mnn = SimpleJsonBuilder.toJson(list)
		}
		cache.addLine(String.format("<input id=\"%s\" type=\"hidden\" name=\"%s\" value=\"%s\" />\n", key, key, StringEscapeUtils.escapeHtml4(mnn)))

		// cache.addLine("\n")
		return null
	}

}
