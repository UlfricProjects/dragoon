package com.ulfric.dragoon.scope;

public final class SingletonScopeStrategy implements ScopeStrategy {

	private Scoped<Object> instance;

	@SuppressWarnings("unchecked")
	@Override
	public <T> Scoped<T> getOrCreate(Class<T> request)
	{
		return this.getOrEmpty(request);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Scoped<T> getOrEmpty(Class<T> request)
	{
		return (Scoped<T>) this.instance;
	}

	public void setInstance(Object instance)
	{
		this.instance = new ReadScoped<>(instance.getClass(), instance);
	}

}