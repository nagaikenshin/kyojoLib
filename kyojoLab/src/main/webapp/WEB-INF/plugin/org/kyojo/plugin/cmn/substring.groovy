package org.kyojo.plugin.cmn

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

class Substring {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		List<Object> al = My.deminion(args, List.class)
		if(al != null && al.size() > 0) {
			String str = al[0].toString()
			if(al.size() > 1) {
				int begin = 0;
				if(al[1] instanceof Number) {
					begin = ((Number)al[1]).intValue()
				}

				if(al.size() > 2) {
					int end = begin + 1;
					if(al[2] instanceof Number) {
						end = ((Number)al[2]).intValue()
					}

					if(begin >= 0 && begin < str.length()
							&& begin < end && end <= str.length()) {
						cache.addLine(str.substring(begin, end))
					}
				} else {
					if(begin >= 0 && begin < str.length()) {
						cache.addLine(str.substring(begin))
					}
				}
			} else {
				cache.addLine(str)
			}
		}
		// cache.addLine("\n")

		return null
	}

}
