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

	public T readOrThrow()
	{
		if (isEmpty())
		{
			throw new IllegalStateException("Failed to create object for request");
		}
		else
		{
			this.read = true;
			return this.value;
		}
	}
	
	public boolean isEmpty() {
		return this.value == null;
	}

	public static <R> Scoped<R> createEmptyScope()
	{
		return new Scoped<>(null);
	}

}