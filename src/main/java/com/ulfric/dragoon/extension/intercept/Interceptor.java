package com.ulfric.dragoon.extension.intercept;

import java.lang.annotation.Annotation;

public abstract class Interceptor {

	private final Annotation declaration;

	public Interceptor(Annotation declaration)
	{
		this.declaration = declaration;
	}

	public final Annotation getDeclaration()
	{
		return this.declaration;
	}

}