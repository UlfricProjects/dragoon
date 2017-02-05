package com.ulfric.commons.cdi.scope;

import com.ulfric.commons.cdi.Scopes;

public abstract class ScopeStrategy {
	
	private Scopes parent;
	
	public abstract <T> Scoped<T> getOrCreate(Class<T> request);
	
	public abstract <T> Scoped<T> getOrEmpty(Class<T> request);
	
	public Scopes getParent()
	{
		return parent;
	}
	
	public void setParent(Scopes parent)
	{
		this.parent = parent;
	}
}