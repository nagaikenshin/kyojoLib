package org.kyojo.core.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public final class OffsetDateTimeExConverter extends AbstractConverter {

	public DateTimeFormatter getStdFormatter() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
	}

	public OffsetDateTimeExConverter() {
		super();
	}

	public OffsetDateTimeExConverter(final Object defaultValue) {
		super(defaultValue);
	}

	@Override
	protected Class<OffsetDateTime> getDefaultType() {
		return OffsetDateTime.class;
	}

	@Override
	protected String convertToString(final Object value) throws Throwable {
		OffsetDateTime odt = toOffsetDateTime(value);

		if(odt == null) {
			return null;
		} else {
			DateTimeFormatter dtf = getStdFormatter();
			return odt.format(dtf);
		}
	}

	protected OffsetDateTime toOffsetDateTime(final Object value) throws Throwable {
		OffsetDateTime odt = null;
		if(value instanceof String) {
			String str = (String)value;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
			try {
				odt = OffsetDateTime.parse(str, dtf);
			} catch(DateTimeParseException dtpe1) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				try {
					Date date = sdf.parse(str);
					odt = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
				} catch(ParseException pe1) {
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						Date date = sdf.parse(str);
						odt = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
					} catch(ParseException pe2) {
						sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
						try {
							Date date = sdf.parse(str);
							odt = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
						} catch(ParseException pe3) {
							sdf = new SimpleDateFormat("yyyy-MM-dd");
							try {
								Date date = sdf.parse(str);
								odt = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
							} catch(ParseException pe4) {}
						}
					}
				}
			}
		} else if(value instanceof OffsetDateTime) {
			odt = (OffsetDateTime)value;
		} else if(value instanceof LocalDateTime) {
			LocalDateTime ldt = (LocalDateTime)value;
			odt = ldt.atOffset(OffsetDateTime.now().getOffset());
		} else if(value instanceof Date) {
			Date date = (Date)value;
			odt = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		}

		if(odt == null) {
			throw new ConversionException("cannot convert value " + value);
		}
		return odt;
	}

	@Override
	protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
		OffsetDateTime odt = toOffsetDateTime(value);
		return OffsetDateTimeToType(type, odt);
	}

	protected <T> T OffsetDateTimeToType(final Class<T> type, final OffsetDateTime odt) throws Throwable {
		if(type.equals(OffsetDateTime.class)) {
			return type.cast(odt);
		} else if(type.equals(LocalDateTime.class)) {
			LocalDateTime ldt = LocalDateTime.ofInstant(odt.toInstant(), odt.getOffset());
			return type.cast(ldt);
		} else if(type.equals(Date.class)) {
			Date date = Date.from(odt.toInstant());
			return type.cast(date);
		} else if(type.equals(Calendar.class)) {
			Calendar cal = GregorianCalendar.from(odt.toZonedDateTime());
			return type.cast(cal);
		} else if(type.equals(java.sql.Timestamp.class)) {
			Date date = Date.from(odt.toInstant());
			return type.cast(new java.sql.Timestamp(date.getTime()));
		} else if(type.equals(LocalDate.class)) {
			LocalDateTime ldt = LocalDateTime.ofInstant(odt.toInstant(), odt.getOffset());
			return type.cast(ldt.toLocalDate());
		} else if(type.equals(java.sql.Date.class)) {
			Date date = Date.from(odt.toInstant());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return type.cast(new java.sql.Date(cal.getTimeInMillis()));
		} else if(type.equals(LocalTime.class)) {
			LocalDateTime ldt = LocalDateTime.ofInstant(odt.toInstant(), odt.getOffset());
			return type.cast(ldt.toLocalTime());
		} else if(type.equals(java.sql.Time.class)) {
			Date date = Date.from(odt.toInstant());
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
