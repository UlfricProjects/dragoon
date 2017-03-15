package com.ulfric.dragoon.intercept;

import java.lang.reflect.Executable;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Callable;

import com.ulfric.commons.exception.Try;

public final class Context {

	public static Context createInvocation(
			Object owner,
			Iterable<Interceptor> pipeline,
			Callable<?> finalDestination,
			Executable destinationExecutable,
			Object[] arguments)
	{
		Objects.requireNonNull(owner);
		Objects.requireNonNull(pipeline);
		Objects.requireNonNull(finalDestination);
		Objects.requireNonNull(destinationExecutable);
		Objects.requireNonNull(arguments);

		Iterator<Interceptor> pipelineIterator = pipeline.iterator();
		Objects.requireNonNull(pipelineIterator);

		return new Context(owner, pipelineIterator, finalDestination, destinationExecutable, arguments);
	}

	private final Object owner;
	private final Iterator<Interceptor> pipeline;
	private final Callable<?> finalDestination;
	private final Executable destinationExecutable;
	private final Object[] arguments;

	private Context(Object owner,
			Iterator<Interceptor> pipeline,
			Callable<?> finalDestination,
			Executable destinationExecutable,
			Object[] arguments)
	{
		this.owner = owner;
		this.pipeline = pipeline;
		this.finalDestination = finalDestination;
		this.destinationExecutable = destinationExecutable;
		this.arguments = arguments;
	}

	public Executable getDestinationExecutable()
	{
		return this.destinationExecutable;
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