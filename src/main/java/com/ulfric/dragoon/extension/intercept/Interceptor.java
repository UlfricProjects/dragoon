package com.ulfric.dragoon.extension.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.concurrent.Callable;

public abstract class Interceptor<T extends Annotation> {

	protected final Executable call;
	protected final T declaration;

	public Interceptor(Executable call, T declaration) {
		this.call = call;
		this.declaration = declaration;
	}

	public abstract Object invoke(Object[] arguments, Callable<?> proceed) throws Exception;

}
