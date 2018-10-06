package org.kyojo.core.converter;

import org.apache.commons.beanutils.converters.AbstractConverter;

public class BooleanExConverter extends AbstractConverter {

	// String化時にtrue/falseではなく1/0に変換する
	// null,"",false,0以外は常にtrueと解釈する

	public BooleanExConverter() {
		super();
	}

	private String[] falseStrings = { "", "false", "0" };

	@Override
	protected Class<Boolean> getDefaultType() {
		return Boolean.class;
	}

	@Override
	protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
		if(Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
			if(value == null) return type.cast(Boolean.FALSE);

			final String stringValue = value.toString().toLowerCase();

			for(String falseString : falseStrings) {
				if(falseString.equals(stringValue)) {
					return type.cast(Boolean.FALSE);
				}
			}

			return type.cast(Boolean.TRUE);
		}

		throw conversionException(type, value);
	}

	@Override
	protected String convertToString(final Object value) throws Throwable {
		if(value == null) return "0";

		if(value instanceof Boolean) {
			Boolean flg = (Boolean)value;
			return flg ? "1" : "0";
		} else {
			String stringValue = value.toString().toLowerCase();
			for(String falseString : falseStrings) {
				if(falseString.equals(stringValue)) {
					return "0";
				}
			}

			return "1";
		}
	}

}
