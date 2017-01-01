package com.ulfric.commons.cdi.intercept;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.exception.ExceptionUtils;

public final class Context<T> {

	public static <T> Builder<T> builder()
	{
		return new Builder<>();
	}

	public static final class Builder<T> implements org.apache.commons.lang3.builder.Builder<Context<T>>
	{
		Builder() { }

		private Method origin;
		private Object[] arguments;
		private Callable<T> destination;
		private List<Interceptor<T>> interceptors;

		@Override
		public Context<T> build()
		{
			Objects.requireNonNull(this.origin);
			Objects.requireNonNull(this.arguments);
			Objects.requireNonNull(this.destination);
			Objects.requireNonNull(this.interceptors);

			return new Context<>(this.origin, this.arguments, this.destination, this.interceptors.iterator());
		}

		public Builder<T> setOrigin(Method origin)
		{
			Objects.requireNonNull(origin);
			this.origin = origin;
			return this;
		}

		public Builder<T> setArguments(Object[] arguments)
		{
			Objects.requireNonNull(arguments);
			this.arguments = arguments;
			return this;
		}

		public Builder<T> setDestination(Callable<T> destination)
		{
			Objects.requireNonNull(destination);
			this.destination = destination;
			return this;
		}

		public Builder<T> setInterceptors(List<Interceptor<T>> interceptors)
		{
			Objects.requireNonNull(interceptors);
			this.interceptors = interceptors;
			return this;
		}
	}

	Context(Method origin, Object[] arguments, Callable<T> destination, Iterator<Interceptor<T>> interceptors)
	{
		this.origin = origin;
		this.arguments = arguments;
		this.destination = destination;
		this.interceptors = interceptors;
	}

	private final Method origin;
	private final Object[] arguments;
	private final Callable<T> destination;
	private final Iterator<Interceptor<T>> interceptors;

	public Method getOrigin()
	{
		return this.origin;
	}

	// TODO allow changing these
	public Object[] getArguments()
	{
		return this.arguments;
	}

	public T proceed()
	{
		if (this.interceptors.hasNext())
		{
			return this.interceptors.next().intercept(this);
		}

		try
		{
			return this.destination.call();
		}
		catch (Exception exception)
		{
			return ExceptionUtils.rethrow(exception);
		}
	}

}