package com.ulfric.commons.cdi.intercept;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public final class InterceptorPipeline {

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<InterceptorPipeline>
	{
		Builder() { }

		private final List<Interceptor> pipeline = new ArrayList<>();

		@Override
		public InterceptorPipeline build()
		{
			List<Interceptor> pipelineCopy = Collections.unmodifiableList(new ArrayList<>(this.pipeline));
			return new InterceptorPipeline(pipelineCopy);
		}

		public Builder addInterceptor(Interceptor interceptor)
		{
			Objects.requireNonNull(interceptor);
			this.pipeline.add(interceptor);
			return this;
		}
	}

	InterceptorPipeline(List<Interceptor> pipeline)
	{
		this.pipeline = pipeline;
	}

	private final List<Interceptor> pipeline;

	public Object call(Object owner, Method origin, Object[] arguments, Callable<?> destination)
	{
		return Context.builder()
				.setOwner(owner)
				.setOrigin(origin)
				.setArguments(arguments)
				.setDestination(destination)
				.setInterceptors(this.pipeline)
				.build()
				.proceed();
	}

}