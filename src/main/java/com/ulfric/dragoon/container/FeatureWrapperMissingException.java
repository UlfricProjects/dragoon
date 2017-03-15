package com.ulfric.dragoon.container;

@SuppressWarnings("serial")
class FeatureWrapperMissingException extends RuntimeException {

	FeatureWrapperMissingException(Class<?> feature)
	{
		super("Failed to create wrapper for " + feature);
	}

}