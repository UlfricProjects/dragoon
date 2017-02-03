package com.ulfric.commons.cdi.scope;

import com.ulfric.commons.cdi.Scopes;
import com.ulfric.commons.cdi.construct.InstanceUtils;

public class DefaultScopeStrategy extends ScopeStrategy {
	
	public static final DefaultScopeStrategy INSTANCE = new DefaultScopeStrategy(null);
	
	DefaultScopeStrategy(Scopes parent)
	{
		super(parent);
	}
	
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