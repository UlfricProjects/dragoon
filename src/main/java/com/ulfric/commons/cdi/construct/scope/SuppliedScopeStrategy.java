package com.ulfric.commons.cdi.construct.scope;

import java.util.Map;
import java.util.Objects;

import com.ulfric.commons.cdi.inject.Injector;
import com.ulfric.commons.collect.MapUtils;

public enum SuppliedScopeStrategy implements ScopeStrategy<Supplied> {

	INSTANCE;

	private final Map<Class<?>, Object> values = MapUtils.newSynchronizedIdentityHashMap();

	@Override
	public <T> T getInstance(Class<T> request, Supplied scope, Injector injector)
	{
		@SuppressWarnings("unchecked")
		T value = (T) this.values.get(request);
		return value;
	}

	public <T> void put(Class<T> request, T implementation)
	{
		Objects.requireNonNull(request);
		Objects.requireNonNull(implementation);

		this.values.put(request, implementation);
	}

}