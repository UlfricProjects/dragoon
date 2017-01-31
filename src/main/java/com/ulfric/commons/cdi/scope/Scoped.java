package com.ulfric.commons.cdi.scope;

public final class Scoped<T> {

	private final Class<T> request;
	private final T value;
	private volatile boolean read;

	public Scoped(Class<T> request, T value)
	{
		this.request = request;
		this.value = value;
	}

	public boolean isRead()
	{
		return this.read;
	}

	public boolean isEmpty()
	{
		return this.value == null;
	}

	public Class<T> getRequest()
	{
		return this.request;
	}

	public T read()
	{
		this.read = true;
		return this.value;
	}

}