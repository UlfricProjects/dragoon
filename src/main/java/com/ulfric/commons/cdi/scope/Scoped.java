package com.ulfric.commons.cdi.scope;

public final class Scoped<T> {

	private final T value;
	private volatile boolean read;

	public Scoped(T value)
	{
		this.value = value;
	}

	public boolean isRead()
	{
		return this.read;
	}

	public T read()
	{
		this.read = true;
		return this.value;
	}

}