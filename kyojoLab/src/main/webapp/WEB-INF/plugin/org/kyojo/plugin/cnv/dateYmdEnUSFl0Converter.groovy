package org.kyojo.plugin.cnv

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

import org.apache.commons.beanutils.ConversionException
import org.kyojo.core.converter.DateExConverter

class DateYmdEnUSFl0Converter extends DateExConverter {

	@Override
	DateTimeFormatter getStdFormatter() {
		return DateTimeFormatter.ofPattern("MMM dd, yyyy").withLocale(new Locale("en", "US"))
	}

	DateYmdEnUSFl0Converter() {
		super()
	}

	DateYmdEnUSFl0Converter(final Object defaultValue) {
		super(defaultValue)
	}

	@Override
	protected LocalDate toLocalDate(final Object value) throws Throwable {
		LocalDate ld = null
		if(value instanceof String) {
			String str = (String)value
			DateTimeFormatter dtf = getStdFormatter()
			try {
				ld = LocalDate.parse(str, dtf)
			} catch(DateTimeParseException dtpe1) {}
		} else if(value instanceof java.sql.Date) {
			java.sql.Date date = (java.sql.Date)value
			ld = date.toLocalDate()
		}

		if(ld == null) {
			throw new ConversionException("cannot convert value " + value)
		}
		return ld
	}

}
