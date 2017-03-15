package com.ulfric.dragoon.scope;

import java.util.NoSuchElementException;

public final class Scoped<T> {

	public static <R> Scoped<R> createEmptyScope(Class<R> request)
	{
		return new Scoped<>(request, null);
	}

	private final Class<T> request;
	private final T value;

	private volatile boolean read;

	public Scoped(Class<T> request, T value)
	{
		this.request = request;
		this.value = value;
	}

	public T read()
	{
		if (this.isEmpty())
		{
			throw new NoSuchElementException("Could read scoped for request: " + request.getName());
		}

		this.read = true;
		return this.value;
	}

	public boolean isRead()
	{
		return this.read;
	}

	public boolean isEmpty()
	{
		return this.value == null;
	}

}