package org.kyojo.plugin.cnv

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import org.apache.commons.beanutils.ConversionException
import org.apache.commons.beanutils.converters.AbstractConverter

class BigDecimalSepC3Scl3HalfUpConverter extends AbstractConverter {

	DecimalFormat getStdFormatter() {
		DecimalFormat df = new DecimalFormat("#,##0.000", new DecimalFormatSymbols(Locale.US))
		df.setParseBigDecimal(true)
		df.setRoundingMode(RoundingMode.HALF_UP)
		return df
	}

	BigDecimalSepC3Scl3HalfUpConverter() {
		super()
	}

	BigDecimalSepC3Scl3HalfUpConverter(final Object defaultValue) {
		super(defaultValue)
	}

	@Override
	protected Class<BigDecimal> getDefaultType() {
		return BigDecimal.class
	}

	@Override
	protected String convertToString(final Object value) throws Throwable {
		BigDecimal bd = toBigDecimal(value)

		if(bd == null) {
			return null
		} else {
			DecimalFormat df = getStdFormatter()
			return df.format(bd.doubleValue())
		}
	}

	protected BigDecimal toBigDecimal(final Object value) throws Throwable {
		BigDecimal bd = null
		if(value instanceof String) {
			String str = (String)value
			DecimalFormat df = getStdFormatter()
			bd = (BigDecimal)df.parse(str);
		} else if(value instanceof BigDecimal) {
			bd = (BigDecimal)value
		}

		if(bd == null) {
			throw new ConversionException("cannot convert value " + value)
		}
		return bd
	}

	@Override
	protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
		BigDecimal bd = toBigDecimal(value)
		return bigDecimalToType(type, bd)
	}

	protected <T> T bigDecimalToType(final Class<T> type, final BigDecimal bd) throws Throwable {
		if(type == BigDecimal.class) {
			return type.cast(bd)
		}

		throw new ConversionException("cannot convert to " + type);
	}

}
