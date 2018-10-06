package org.kyojo.core.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractConverter;

public class DateExConverter extends AbstractConverter {

	public DateTimeFormatter getStdFormatter() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd");
	}

	public DateExConverter() {
		super();
	}

	public DateExConverter(final Object defaultValue) {
		super(defaultValue);
	}

	@Override
	protected Class<java.sql.Date> getDefaultType() {
		return java.sql.Date.class;
	}

	@Override
	protected String convertToString(final Object value) throws Throwable {
		LocalDate ld = toLocalDate(value);

		if(ld == null) {
			return null;
		} else {
			DateTimeFormatter dtf = getStdFormatter();
			return ld.format(dtf);
		}
	}

	protected LocalDate toLocalDate(final Object value) throws Throwable {
		LocalDate ld = null;
		if(value instanceof String) {
			String str = (String)value;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			try {
				ld = LocalDate.parse(str, dtf);
			} catch(DateTimeParseException dtpe1) {}
		} else if(value instanceof java.sql.Date) {
			java.sql.Date date = (java.sql.Date)value;
			ld = date.toLocalDate();
		} else if(value instanceof LocalDate) {
			ld = (LocalDate)value;
		} else if(value instanceof Date) {
			Date date = (Date)value;
			LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			ld = ldt.toLocalDate();
		}

		if(ld == null) {
			throw new ConversionException("cannot convert value " + value);
		}
		return ld;
	}

	@Override
	protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
		LocalDate ld = toLocalDate(value);
		return localDateToType(type, ld);
	}

	protected <T> T localDateToType(final Class<T> type, final LocalDate ld) throws Throwable {
		if(type.equals(LocalDate.class)) {
			return type.cast(ld);
		} else if(type.equals(LocalDateTime.class)) {
			LocalDateTime ldt = ld.atStartOfDay();
			return type.cast(ldt);
		} else if(type.equals(OffsetDateTime.class)) {
			LocalDateTime ldt = ld.atStartOfDay();
			OffsetDateTime odt = ldt.atOffset(OffsetDateTime.now().getOffset());
			return type.cast(odt);
		} else if(type.equals(Date.class)) {
			LocalDateTime ldt = ld.atStartOfDay();
			Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			return type.cast(date);
		} else if(type.equals(Calendar.class)) {
			LocalDateTime ldt = ld.atStartOfDay();
			OffsetDateTime odt = ldt.atOffset(OffsetDateTime.now().getOffset());
			Calendar cal = GregorianCalendar.from(odt.toZonedDateTime());
			return type.cast(cal);
		} else if(type.equals(java.sql.Timestamp.class)) {
			LocalDateTime ldt = ld.atStartOfDay();
			Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			return type.cast(new java.sql.Timestamp(date.getTime()));
		} else if(type.equals(java.sql.Date.class)) {
			LocalDateTime ldt = ld.atStartOfDay();
			Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return type.cast(new java.sql.Date(cal.getTimeInMillis()));
		}

		throw new ConversionException("cannot convert to " + type);
	}

}
