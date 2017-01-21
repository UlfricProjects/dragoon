package com.ulfric.commons.cdi.intercept;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Callable;

import com.ulfric.commons.exception.Try;

public final class Invocation {

	public static Invocation createInvocation(
			Object owner,
			Iterable<Interceptor> pipeline,
			Callable<?> finalDestination,
			Object[] arguments)
	{
		Objects.requireNonNull(owner);
		Objects.requireNonNull(pipeline);
		Objects.requireNonNull(finalDestination);
		Objects.requireNonNull(arguments);

		Iterator<Interceptor> pipelineIterator = pipeline.iterator();
		Objects.requireNonNull(pipelineIterator);

		return new Invocation(owner, pipelineIterator, finalDestination, arguments);
	}

	private final Object owner;
	private final Iterator<Interceptor> pipeline;
	private final Callable<?> finalDestination;
	private final Object[] arguments;

	private Invocation(Object owner, Iterator<Interceptor> pipeline, Callable<?> finalDestination, Object[] arguments)
	{
		this.owner = owner;
		this.pipeline = pipeline;
		this.finalDestination = finalDestination;
		this.arguments = arguments;
	}

	public Object getOwner()
	{
		return this.owner;
	}

	public Object[] getArguments()
	{
		return this.arguments;
	}

	public Object proceed()
	{
		if (this.pipeline.hasNext())
		{
			return this.pipeline.next().intercept(this);
		}

		return Try.to(this.finalDestination::call);
	}

}