package com.ulfric.commons.cdi.scope;

public interface ScopeStrategy {

	<T> Scoped<T> getOrCreate(Class<T> request);

	<T> Scoped<T> getOrEmpty(Class<T> request);

}