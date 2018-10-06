package org.kyojo.plugin.cmn

import org.apache.commons.lang3.StringUtils

import org.kyojo.core.Cache

class ViewListerSimple extends ThingLister {

	@Override
	public String getName() {
		return StringUtils.uncapitalize(getClass().getSimpleName())
	}

	@Override
	void addParentHtmlLines(Cache cache) {
		cache.addLine("<table class=\"collection " + getName() + "\">\n")
		// 描画はJavaScript
		cache.addLine("</table>\n")
	}

}
