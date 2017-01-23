package com.ulfric.commons.cdi.container;

@SuppressWarnings("serial")
class ComponentWrapperMissingException extends RuntimeException {

	ComponentWrapperMissingException(Class<?> component)
	{
		super("Failed to create wrapper for " + component);
	}

}