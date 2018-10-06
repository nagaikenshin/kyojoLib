package org.kyojo.plugin.cmn

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle

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

class Date2Ymdhms {

	String lcLng
	String lcRgn
	String date

	Locale buildLocale() {
		if(StringUtils.isBlank(lcLng)) {
			return Locale.default
		} else {
			if(StringUtils.isBlank(lcRgn)) {
				return new Locale(lcLng)
			} else {
				return new Locale(lcLng, lcRgn)
			}
		}
	}

	String format(OffsetDateTime odt, Locale locale) {
		DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale)
		odt.format(dtf)
	}

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(StringUtils.isBlank(args)) return null

		if(args.indexOf('{') < 0 && args.indexOf('[') < 0) {
			String[] aes = args.split(",")
			date = aes[0]
			if(aes.length > 1) lcLng = aes[1]
			if(aes.length > 2) lcRgn = aes[2]
		}

		Locale locale = buildLocale()

		OffsetDateTime odt = null
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
		Date dt = null
		try {
			odt = OffsetDateTime.parse(date, dtf)
		} catch(DateTimeParseException dtpe1) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
			try {
				dt = sdf.parse(date)
				odt = OffsetDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault())
			} catch(ParseException pe1) {
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				try {
					dt = sdf.parse(date)
					odt = OffsetDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault())
				} catch(ParseException pe2) {
					sdf = new SimpleDateFormat("yyyy-MM-dd")
					try {
						dt = sdf.parse(date)
						odt = OffsetDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault())
					} catch(ParseException pe3) {
						sdf = new SimpleDateFormat("HH:mm:ss")
						try {
							dt = sdf.parse(date)
							odt = OffsetDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault())
						} catch(ParseException pe4) {
							sdf = new SimpleDateFormat("HH:mm")
							try {
								dt = sdf.parse(date)
								odt = OffsetDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault())
							} catch(ParseException pe5) {}
						}
					}
				}
			}
		}

		if(odt != null) {
			cache.addLine(format(odt, locale))
		}

		return null
	}

}
