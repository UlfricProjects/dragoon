package com.ulfric.commons.cdi.construct;

@SuppressWarnings("serial")
public class IllegalRequestExactException extends RuntimeException {

	public IllegalRequestExactException(Class<?> request, Object value)
	{
		super("Attempted to request exact type " + request + ", value was of type "
				+ (value == null ? "null" : value.getClass().getName()));
	}

}