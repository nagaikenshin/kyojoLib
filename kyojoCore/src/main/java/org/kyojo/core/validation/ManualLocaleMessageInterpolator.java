package org.kyojo.core.validation;

import java.util.Locale;

import javax.validation.MessageInterpolator;

public class ManualLocaleMessageInterpolator implements MessageInterpolator {

	private final MessageInterpolator delegate;
	private Locale locale;

	public ManualLocaleMessageInterpolator(MessageInterpolator delegate, Locale locale) {
		super();
		this.delegate = delegate;
		this.locale = locale;
	}

	@Override
	public String interpolate(String messageTemplate, Context context) {
		if(this.locale == null) {
			return delegate.interpolate(messageTemplate, context);
		} else {
			return delegate.interpolate(messageTemplate, context, locale);
		}
	}

	@Override
	public String interpolate(String messageTemplate, Context context, Locale locale) {
		if(this.locale == null) {
			return delegate.interpolate(messageTemplate, context, locale);
		} else {
			return delegate.interpolate(messageTemplate, context, this.locale);
		}
	}

}
