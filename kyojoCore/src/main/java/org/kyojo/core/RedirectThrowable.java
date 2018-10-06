package org.kyojo.core;

public class RedirectThrowable extends Throwable {

	private static final long serialVersionUID = 1L;

	private Object rdctTo;

	public RedirectThrowable(Object rdctTo) {
		this.rdctTo = rdctTo;
	}

	public Object getRedirectTo() {
		return rdctTo;
	}
}
