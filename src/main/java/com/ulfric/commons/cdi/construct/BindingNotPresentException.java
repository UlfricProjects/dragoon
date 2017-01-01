package com.ulfric.commons.cdi.construct;

@SuppressWarnings("serial")
public class BindingNotPresentException extends RuntimeException {

	public BindingNotPresentException(Class<?> requested)
	{
		super("Requested type: " + requested);
	}

}