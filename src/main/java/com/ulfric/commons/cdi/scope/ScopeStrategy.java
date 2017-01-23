package com.ulfric.commons.cdi.scope;

public interface ScopeStrategy {

	<T> Scoped<T> getOrCreate(Class<T> request);

}