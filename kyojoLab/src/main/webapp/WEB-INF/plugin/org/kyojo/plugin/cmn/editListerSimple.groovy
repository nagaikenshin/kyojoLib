package org.kyojo.plugin.cmn

import org.apache.commons.lang3.StringUtils

import org.kyojo.core.Cache

class EditListerSimple extends ThingLister {

	@Override
	String getName() {
		return StringUtils.uncapitalize(getClass().getSimpleName())
	}

	@Override
	void addParentHtmlLines(Cache cache) {
		cache.addLine("<ul class=\"collection " + getName() + "\">\n")
		// 描画はJavaScript
		cache.addLine("</ul>\n")
	}

}
