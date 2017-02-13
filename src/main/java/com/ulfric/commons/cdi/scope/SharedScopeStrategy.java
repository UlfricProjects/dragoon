package com.ulfric.commons.cdi.scope;

import java.util.IdentityHashMap;
import java.util.Map;

import com.ulfric.commons.cdi.construct.InstanceUtils;

public final class SharedScopeStrategy implements ScopeStrategy {

	private final Map<Class<?>, Object> sharedObjects = new IdentityHashMap<>();

	@Override
	public <T> Scoped<T> getOrCreate(Class<T> request)
	{
		@SuppressWarnings("unchecked")
		T instance = (T) this.sharedObjects.computeIfAbsent(request, InstanceUtils::createOrNull);
		return new Scoped<>(request, instance);
	}

	@Override
	public <T> Scoped<T> getOrEmpty(Class<T> request)
	{
		@SuppressWarnings("unchecked")
		T instance = (T) this.sharedObjects.getOrDefault(request, null);
		if (instance == null)
		{
			return Scoped.createEmptyScope(request);
		}
		else
		{
			return new Scoped<>(request, instance);
		}
	}
}