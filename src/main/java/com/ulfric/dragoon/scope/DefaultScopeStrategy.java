package com.ulfric.dragoon.scope;

import com.ulfric.dragoon.construct.InstanceUtils;

public enum DefaultScopeStrategy implements ScopeStrategy {

	INSTANCE;

	@Override
	public <T> Scoped<T> getOrCreate(Class<T> request)
	{
		T instance = InstanceUtils.createOrNull(request);
		return new Scoped<>(request, instance);
	}

	@Override
	public <T> Scoped<T> getOrEmpty(Class<T> request)
	{
		return getOrCreate(request);
	}

}