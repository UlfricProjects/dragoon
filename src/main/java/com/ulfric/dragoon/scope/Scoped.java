package com.ulfric.dragoon.scope;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class Scoped<T> {

	public static <T> Scoped<T> createEmptyScope(Class<T> request)
	{
		return new Scoped<>(request, null);
	}

	private final Class<? extends T> request;
	private final T value;

	private Set<String> reads;

	public Scoped(Class<? extends T> request, T value)
	{
		this.request = request;
		this.value = value;
	}

	public T read()
	{
		return this.read(null);
	}

	public T read(String type)
	{
		if (this.isEmpty())
		{
			throw new NoSuchElementException("Could read scoped for request: " + request.getName());
		}

		this.markRead(type);
		return this.value;
	}

	private void markRead(String type)
	{
		this.makeReadable();
		this.reads.add(type);
	}

	private void makeReadable()
	{
		if (this.reads == null)
		{
			this.reads = new HashSet<>();
		}
	}

	public boolean isRead()
	{
		return this.reads != null;
	}

	public boolean isRead(String type)
	{
		return this.isRead() && this.reads.contains(type);
	}

	public boolean isEmpty()
	{
		return this.value == null;
	}

}