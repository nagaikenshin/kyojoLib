package org.kyojo.plugin.cnv

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

import org.apache.commons.beanutils.ConversionException
import org.apache.commons.lang3.StringUtils
import org.kyojo.core.converter.LocalDateExConverter
import org.kyojo.plugin.cmn.Date2YgmdJa

class LocalDateYgmdJaDl0Converter extends LocalDateExConverter {

	@Override
	DateTimeFormatter getStdFormatter() {
		return DateTimeFormatter.ofPattern("yyyy'年'M'月'd'日'").withLocale(new Locale("ja"))
	}

	LocalDateYgmdJaDl0Converter() {
		super()
	}

	LocalDateYgmdJaDl0Converter(final Object defaultValue) {
		super(defaultValue)
	}

	@Override
	protected String convertToString(final Object value) throws Throwable {
		LocalDate ld = toLocalDate(value)

		if(ld == null) {
			return null
		} else {
			DateTimeFormatter dtf = getStdFormatter()
			String str = ld.format(dtf)
			String gengo = Date2YgmdJa.getGengo(ld)
			if(StringUtils.isBlank(gengo)) {
				return str
			} else {
				return str.replaceAll("^(\\d+)", "\$1（${gengo}年）")
			}
		}
	}

	@Override
	protected LocalDate toLocalDate(final Object value) throws Throwable {
		LocalDate ld = null
		if(value instanceof String) {
			String str = (String)value
			String str2 = str.replaceAll("^(\\d+)（.*）", '$1')
			DateTimeFormatter dtf = getStdFormatter()
			try {
				ld = LocalDate.parse(str2, dtf)
			} catch(DateTimeParseException dtpe1) {}
		} else if(value instanceof LocalDate) {
			ld = (LocalDate)value
		}

		if(ld == null) {
			throw new ConversionException("cannot convert value " + value)
		}
		return ld
	}

}
