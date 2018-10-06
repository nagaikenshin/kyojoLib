package org.kyojo.plugin.cnv

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

import org.apache.commons.beanutils.ConversionException
import org.kyojo.core.converter.TimeExConverter

class TimeHmDl0Converter extends TimeExConverter {

	@Override
	DateTimeFormatter getStdFormatter() {
		return DateTimeFormatter.ofPattern("HH:mm").withLocale(new Locale("en"))
	}

	TimeHmDl0Converter() {
		super()
	}

	TimeHmDl0Converter(final Object defaultValue) {
		super(defaultValue)
	}

	@Override
	protected LocalTime toLocalTime(final Object value) throws Throwable {
		LocalTime lt = null
		if(value instanceof String) {
			String str = (String)value
			DateTimeFormatter dtf = getStdFormatter()
			try {
				lt = LocalTime.parse(str, dtf)
			} catch(DateTimeParseException dtpe1) {}
		} else if(value instanceof java.sql.Time) {
			java.sql.Time time = (java.sql.Time)value
			lt = time.toLocalTime()
		}

		if(lt == null) {
			throw new ConversionException("cannot convert value " + value)
		}
		return lt
	}

}
