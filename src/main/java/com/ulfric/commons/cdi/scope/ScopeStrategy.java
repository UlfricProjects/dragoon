package com.ulfric.commons.cdi.scope;

import com.ulfric.commons.cdi.Scopes;

public abstract class ScopeStrategy {
	
	protected final Scopes parent;
	
	protected ScopeStrategy(Scopes parent)
	{
		this.parent = parent;
	}
	
	public abstract <T> Scoped<T> getOrCreate(Class<T> request);
	
	public abstract <T> Scoped<T> getOrEmpty(Class<T> request);
	
}