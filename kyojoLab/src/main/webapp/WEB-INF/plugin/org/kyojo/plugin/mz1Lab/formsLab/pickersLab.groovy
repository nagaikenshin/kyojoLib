package org.kyojo.plugin.mz1Lab.formsLab

import java.time.LocalDate
import java.time.LocalTime

import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.SpecifiedConverter
import org.kyojo.plugin.cnv.LocalDateYmdEnUSFl0Converter
import org.kyojo.plugin.cnv.LocalTimeHmEnUSFl0Converter

class PickersLab {

	@OutOfRequestData
	String title
	@SpecifiedConverter(LocalDateYmdEnUSFl0Converter.class)
	LocalDate date
	@SpecifiedConverter(LocalTimeHmEnUSFl0Converter.class)
	LocalTime time

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		title = "Forms Pickers"

		return null
	}

}
