package com.ulfric.commons.cdi.intercept;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

public final class BytebuddyInterceptor<T> {

	public static <T> BytebuddyInterceptor<T> newInstance(InterceptorPipeline pipeline)
	{
		Objects.requireNonNull(pipeline);

		return new BytebuddyInterceptor<>(pipeline);
	}

	private final InterceptorPipeline pipeline;

	private BytebuddyInterceptor(InterceptorPipeline pipeline)
	{
		this.pipeline = pipeline;
	}

	@RuntimeType
	public Object intercept(@This Object owner,
							@Origin Method origin,
							@AllArguments Object[] arguments,
							@SuperCall Callable<T> destination)
	{
		return this.pipeline.call(owner, origin, arguments, destination);
	}

}