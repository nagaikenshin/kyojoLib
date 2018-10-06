package org.kyojo.plugin.miscLab

import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

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
import org.kyojo.core.annotation.SpecifiedConverter
import org.kyojo.plugin.cnv.DateYmdEnUSFl0Converter
import org.kyojo.plugin.cnv.BigDecimalSepC3Scl3HalfUpConverter
import org.kyojo.plugin.cnv.LocalDateYgmdJaDl0Converter
import org.kyojo.plugin.cnv.LocalTimeHmDl0Converter
import org.kyojo.plugin.cnv.TimeHmEnUSFl0Converter

class ConversionLab {

	Date dateTime1
	OffsetDateTime dateTime2
	Timestamp dateTime3
	java.sql.Date date1
	LocalDate date2
	@SpecifiedConverter(DateYmdEnUSFl0Converter.class)
	java.sql.Date date3
	@SpecifiedConverter(LocalDateYgmdJaDl0Converter.class)
	LocalDate date4
	java.sql.Time time1
	LocalTime time2
	@SpecifiedConverter(TimeHmEnUSFl0Converter.class)
	java.sql.Time time3
	@SpecifiedConverter(LocalTimeHmDl0Converter.class)
	LocalTime time4
	@SpecifiedConverter(BigDecimalSepC3Scl3HalfUpConverter.class)
	List<BigDecimal> bdList

	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		dateTime2 = OffsetDateTime.now()
		dateTime1 = Date.from(dateTime2.toInstant())
		dateTime3 = new Timestamp(dateTime1.getTime())
		date2 = LocalDate.now()
		date1 = java.sql.Date.valueOf(date2)
		date3 = date1
		date4 = date2
		time2 = LocalTime.now()
		time1 = java.sql.Time.valueOf(time2)
		time3 = time1
		time4 = time2
		bdList = [ BigDecimal.ZERO ]

		return null
	}

	Object doSubmit(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		println(dateTime1)
		println(dateTime2)
		println(dateTime3)
		println(date1)
		println(date2)
		println(date3)
		println(date4)
		println(time1)
		println(time2)
		println(time3)
		println(time4)

		return null
	}

}
