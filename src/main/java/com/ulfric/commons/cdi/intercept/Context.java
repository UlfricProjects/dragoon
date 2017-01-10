package com.ulfric.commons.cdi.intercept;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public final class Context {

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Context>
	{
		private Object owner;
		private Method origin;
		private Object[] arguments;
		private Callable<?> destination;
		private List<Interceptor> interceptors;

		Builder() { }

		@Override
		public Context build()
		{
			Objects.requireNonNull(this.owner);
			Objects.requireNonNull(this.origin);
			Objects.requireNonNull(this.arguments);
			Objects.requireNonNull(this.destination);
			Objects.requireNonNull(this.interceptors);

			return new Context(this.owner, this.origin, this.arguments,
					this.destination, this.interceptors.iterator());
		}

		public Builder setOwner(Object owner)
		{
			Objects.requireNonNull(owner);
			this.owner = owner;
			return this;
		}

		public Builder setOrigin(Method origin)
		{
			Objects.requireNonNull(origin);
			this.origin = origin;
			return this;
		}

		public Builder setArguments(Object[] arguments)
		{
			Objects.requireNonNull(arguments);
			this.arguments = arguments;
			return this;
		}

		public Builder setDestination(Callable<?> destination)
		{
			Objects.requireNonNull(destination);
			this.destination = destination;
			return this;
		}

		public Builder setInterceptors(List<Interceptor> interceptors)
		{
			Objects.requireNonNull(interceptors);
			this.interceptors = interceptors;
			return this;
		}
	}

	private final Object owner;
	private final Method origin;
	private final Object[] arguments;
	private final Callable<?> destination;
	private final Iterator<Interceptor> interceptors;


	Context(Object owner, Method origin, Object[] arguments,
			Callable<?> destination, Iterator<Interceptor> interceptors)
	{
		this.owner = owner;
		this.origin = origin;
		this.arguments = arguments;
		this.destination = destination;
		this.interceptors = interceptors;
	}
	public Object getOwner()
	{
		return this.owner;
	}

	public Method getOrigin()
	{
		return this.origin;
	}

	// TODO allow changing these
	public Object[] getArguments()
	{
		return this.arguments;
	}

	public Object proceed()
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
			throw new RuntimeException(exception);
		}
	}

}