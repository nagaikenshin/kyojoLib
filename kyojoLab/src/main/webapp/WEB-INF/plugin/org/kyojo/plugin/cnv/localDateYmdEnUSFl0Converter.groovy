package org.kyojo.plugin.cnv

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

import org.apache.commons.beanutils.ConversionException
import org.kyojo.core.converter.LocalDateExConverter

class LocalDateYmdEnUSFl0Converter extends LocalDateExConverter {

	@Override
	DateTimeFormatter getStdFormatter() {
		return DateTimeFormatter.ofPattern("MMM dd, yyyy").withLocale(new Locale("en", "US"))
	}

	LocalDateYmdEnUSFl0Converter() {
		super()
	}

	LocalDateYmdEnUSFl0Converter(final Object defaultValue) {
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
		} else if(value instanceof LocalDate) {
			ld = (LocalDate)value
		}

		if(ld == null) {
			throw new ConversionException("cannot convert value " + value)
		}
		return ld
	}

}
