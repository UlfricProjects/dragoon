package com.ulfric.commons.cdi.intercept;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public final class BytebuddyInterceptor<T> {

	public static <T> BytebuddyInterceptor<T> newInstance(InterceptorPipeline<T> pipeline)
	{
		Objects.requireNonNull(pipeline);

		return new BytebuddyInterceptor<>(pipeline);
	}

	private BytebuddyInterceptor(InterceptorPipeline<T> pipeline)
	{
		this.pipeline = pipeline;
	}

	private final InterceptorPipeline<T> pipeline;

	@RuntimeType
	public Object intercept(@Origin Method origin,
							@AllArguments Object[] arguments,
							@SuperCall Callable<T> destination)
	{
		return this.pipeline.call(origin, arguments, destination);
	}

}