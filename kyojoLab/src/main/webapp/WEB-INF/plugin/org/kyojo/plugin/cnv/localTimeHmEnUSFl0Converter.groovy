package org.kyojo.plugin.cnv

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

import org.apache.commons.beanutils.ConversionException
import org.kyojo.core.converter.LocalTimeExConverter

class LocalTimeHmEnUSFl0Converter extends LocalTimeExConverter {

	@Override
	DateTimeFormatter getStdFormatter() {
		return DateTimeFormatter.ofPattern("hh:mm a").withLocale(new Locale("en", "US"))
	}

	LocalTimeHmEnUSFl0Converter() {
		super()
	}

	LocalTimeHmEnUSFl0Converter(final Object defaultValue) {
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
		} else if(value instanceof LocalTime) {
			lt = (LocalTime)value
		}

		if(lt == null) {
			throw new ConversionException("cannot convert value " + value)
		}
		return lt
	}

}
