package com.ulfric.commons.cdi.scope;

import com.ulfric.commons.cdi.construct.InstanceUtils;

public enum DefaultScopeStrategy implements ScopeStrategy {

	INSTANCE;

	@Override
	public <T> Scoped<T> getOrCreate(Class<T> request)
	{
		T instance = InstanceUtils.createOrNull(request);
		return new Scoped<>(instance);
	}

}