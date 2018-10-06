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
import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.plugin.html5.OptGroupElement
import org.kyojo.plugin.html5.OptionElement

class SelectLab {

	Integer hour24
	@OutOfRequestData
	List<OptionElement> hour24Options
	Integer hourAmPm
	@OutOfRequestData
	List<OptGroupElement> hourAmPmOptGroups
	List<String> prefectures
	@OutOfRequestData
	List<OptionElement> prefecturesOptions

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		hour24Options = []
		( 0..23 ).each {
			hour24Options.add(new OptionElement(it))
		}

		hourAmPmOptGroups = []
		def hodOptions = [ [], [] ]
		( 0..11 ).each {
			hodOptions[0].add(new OptionElement(it, it))
		}
		( 0..11 ).each {
			hodOptions[1].add(new OptionElement(it + 12, it))
		}
		[ "a.m.", "p.m." ].eachWithIndex { label, idx ->
			hourAmPmOptGroups.add(new OptGroupElement(label, hodOptions[idx]))
		}

		prefecturesOptions = [
			new OptionElement("08", "茨城県(Ibaraki-ken)"),
			new OptionElement("09", "栃木県(Tochigi-ken)"),
			new OptionElement("10", "群馬県(Gunma-ken)"),
			new OptionElement("11", "埼玉県(Saitama-ken)"),
			new OptionElement("12", "千葉県(Chiba-ken)"),
			new OptionElement("13", "東京都(Tokyo-to)"),
			new OptionElement("14", "神奈川県(Kanagawa-ken)")
		]

		return null
	}

	Object doSelect(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		return null
	}

}
