package com.ulfric.commons.cdi.intercept;

@SuppressWarnings("serial")
public final class FauxInterceptorException extends RuntimeException {

	public FauxInterceptorException(Object fakeInterceptor)
	{
		super(String.valueOf(fakeInterceptor));
	}

}