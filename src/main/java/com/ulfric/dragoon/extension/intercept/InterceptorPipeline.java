package com.ulfric.dragoon.extension.intercept;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public final class InterceptorPipeline {

	private final List<Interceptor<?>> interceptors;

	public InterceptorPipeline(List<Interceptor<?>> interceptors)
	{
		this.interceptors = interceptors;
	}

	@RuntimeType
	public Object intercept(@AllArguments Object[] arguments,
			@SuperCall Callable<?> finalDestination) throws Exception
	{
		return new Invocation(this.interceptors.iterator(), arguments, finalDestination).call();
	}

	private static final class Invocation implements Callable<Object>
	{
		private final Iterator<Interceptor<?>> pipeline;
		private final Object[] arguments;
		private final Callable<?> finalDestination;

		Invocation(Iterator<Interceptor<?>> pipeline, Object[] arguments, Callable<?> finalDestination)
		{
			this.pipeline = pipeline;
			this.arguments = arguments;
			this.finalDestination = finalDestination;
		}

		@Override
		public Object call() throws Exception
		{
			if (this.pipeline.hasNext())
			{
				return this.pipeline.next().invoke(this.arguments, this);
			}

			return this.finalDestination.call();
		}
	}

}