package com.ulfric.dragoon.extension.intercept;

import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;

public abstract class Interceptor<T extends Annotation> {

	private final T declaration;

	public Interceptor(T declaration)
	{
		this.declaration = declaration;
	}

	public final T getDeclaration()
	{
		return this.declaration;
	}

	public abstract Object invoke(Object[] arguments, Callable<?> proceed) throws Exception;

}