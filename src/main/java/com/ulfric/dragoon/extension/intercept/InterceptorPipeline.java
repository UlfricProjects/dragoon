package com.ulfric.dragoon.extension.intercept;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

final class InterceptorPipeline {

	private final List<Interceptor> interceptors;

	public InterceptorPipeline(List<Interceptor> interceptors)
	{
		this.interceptors = interceptors;
	}

	@RuntimeType
	public Object intercept(@This Object owner,
	                        @AllArguments Object[] arguments,
	                        @SuperCall Callable<?> finalDestination,
	                        @Origin Method destinationExecutable) throws Exception
	{
		System.out.println(destinationExecutable);
		return finalDestination.call();
	}

}