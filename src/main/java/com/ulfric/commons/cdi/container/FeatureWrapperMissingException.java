package com.ulfric.commons.cdi.container;

@SuppressWarnings("serial")
class FeatureWrapperMissingException extends RuntimeException {

	FeatureWrapperMissingException(Class<?> feature)
	{
		super("Failed to create wrapper for " + feature);
	}

}