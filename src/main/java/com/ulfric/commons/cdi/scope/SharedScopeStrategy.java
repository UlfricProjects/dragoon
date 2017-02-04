package com.ulfric.commons.cdi.scope;

import com.ulfric.commons.cdi.Scopes;
import com.ulfric.commons.cdi.construct.InstanceUtils;

import java.util.IdentityHashMap;
import java.util.Map;

public final class SharedScopeStrategy extends ScopeStrategy {

	private final Map<Class<?>, Object> sharedObjects = new IdentityHashMap<>();
	
	protected SharedScopeStrategy(Scopes parent)
	{
		super(parent);
	}
	
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