package org.kyojo.core.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractConverter;

public class TimeExConverter extends AbstractConverter {

	public DateTimeFormatter getStdFormatter() {
		return DateTimeFormatter.ofPattern("HH:mm:ss");
	}

	public TimeExConverter() {
		super();
	}

	public TimeExConverter(final Object defaultValue) {
		super(defaultValue);
	}

	@Override
	protected Class<java.sql.Time> getDefaultType() {
		return java.sql.Time.class;
	}

	@Override
	protected String convertToString(final Object value) throws Throwable {
		LocalTime lt = toLocalTime(value);

		if(lt == null) {
			return null;
		} else {
			DateTimeFormatter dtf = getStdFormatter();
			return lt.format(dtf);
		}
	}

	protected LocalTime toLocalTime(final Object value) throws Throwable {
		LocalTime lt = null;
		if(value instanceof String) {
			String str = (String)value;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
			try {
				lt = LocalTime.parse(str, dtf);
			} catch(DateTimeParseException dtpe1) {
				dtf = DateTimeFormatter.ofPattern("HH:mm");
				try {
					lt = LocalTime.parse(str, dtf);
				} catch(DateTimeParseException dtpe2) {}
			}
		} else if(value instanceof java.sql.Time) {
			java.sql.Time time = (java.sql.Time)value;
			lt = time.toLocalTime();
		} else if(value instanceof LocalTime) {
			lt = (LocalTime)value;
		} else if(value instanceof Date) {
			Date date = (Date)value;
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.YEAR, 1970);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DATE, 1);
			java.sql.Time time = new java.sql.Time(cal.getTimeInMillis());
			lt = time.toLocalTime();
		}

		if(lt == null) {
			throw new ConversionException("cannot convert value " + value);
		}
		return lt;
	}

	@Override
	protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
		LocalTime lt = toLocalTime(value);
		return localTimeToType(type, lt);
	}

	protected <T> T localTimeToType(final Class<T> type, final LocalTime lt) throws Throwable {
		if(type.equals(LocalTime.class)) {
			return type.cast(lt);
		}

		LocalDate ld = LocalDate.of(1970, 1, 1);
		LocalDateTime ldt = LocalDateTime.of(ld, lt);

		if(type.equals(LocalDateTime.class)) {
			return type.cast(ldt);
		} else if(type.equals(OffsetDateTime.class)) {
			OffsetDateTime odt = ldt.atOffset(OffsetDateTime.now().getOffset());
			return type.cast(odt);
		} else if(type.equals(Date.class)) {
			Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			return type.cast(date);
		} else if(type.equals(Calendar.class)) {
			OffsetDateTime odt = ldt.atOffset(OffsetDateTime.now().getOffset());
			Calendar cal = GregorianCalendar.from(odt.toZonedDateTime());
			return type.cast(cal);
		} else if(type.equals(java.sql.Timestamp.class)) {
			Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			return type.cast(new java.sql.Timestamp(date.getTime()));
		} else if(type.equals(java.sql.Time.class)) {
			Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.YEAR, 1970);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DATE, 1);
			return type.cast(new java.sql.Time(cal.getTimeInMillis()));
		}

		throw new ConversionException("cannot convert to " + type);
	}

}
