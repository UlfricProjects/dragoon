package com.ulfric.dragoon.scope;

public final class ReadScoped<T> extends Scoped<T> {

	public ReadScoped(Class<? extends T> request, T value)
	{
		super(request, value);
	}

	@Override
	public boolean isRead()
	{
		return true;
	}

	@Override
	public boolean isRead(String name)
	{
		return true;
	}

}