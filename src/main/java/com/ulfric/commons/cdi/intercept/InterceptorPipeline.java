package com.ulfric.commons.cdi.intercept;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public final class InterceptorPipeline<T> {

	public static <T> Builder<T> builder()
	{
		return new Builder<>();
	}

	public static final class Builder<T> implements org.apache.commons.lang3.builder.Builder<InterceptorPipeline<T>>
	{
		Builder() { }

		private final List<Interceptor<T>> pipeline = new ArrayList<>();

		@Override
		public InterceptorPipeline<T> build()
		{
			List<Interceptor<T>> pipelineCopy = Collections.unmodifiableList(new ArrayList<>(this.pipeline));
			return new InterceptorPipeline<>(pipelineCopy);
		}

		public Builder<T> addInterceptor(Interceptor<T> interceptor)
		{
			Objects.requireNonNull(interceptor);
			this.pipeline.add(interceptor);
			return this;
		}
	}

	InterceptorPipeline(List<Interceptor<T>> pipeline)
	{
		this.pipeline = pipeline;
	}

	private final List<Interceptor<T>> pipeline;

	public T call(Method origin, Object[] arguments, Callable<T> destination)
	{
		return Context.<T>builder()
				.setOrigin(origin)
				.setArguments(arguments)
				.setDestination(destination)
				.setInterceptors(this.pipeline)
				.build()
				.proceed();
	}

}