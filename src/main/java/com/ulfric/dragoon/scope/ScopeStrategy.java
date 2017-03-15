package com.ulfric.dragoon.scope;

public interface ScopeStrategy {

	<T> Scoped<T> getOrCreate(Class<T> request);

	<T> Scoped<T> getOrEmpty(Class<T> request);

}