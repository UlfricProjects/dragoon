package com.ulfric.commons.cdi.construct.scope;

import java.lang.annotation.Annotation;

public enum DefaultImpl implements Default {

	INSTANCE;

	@Override
	public Class<? extends Annotation> annotationType()
	{
		return Default.class;
	}

}