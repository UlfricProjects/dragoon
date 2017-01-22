package com.ulfric.commons.cdi.container;

@SuppressWarnings("serial")
public class ComponentWrapperMissingException extends RuntimeException {

	public ComponentWrapperMissingException(Class<?> component)
	{
		super("Failed to create wrapper for " + component);
	}

}