package com.ulfric.commons.cdi.construct.scope;

import java.util.Map;

import com.ulfric.commons.cdi.construct.InstanceUtils;
import com.ulfric.commons.cdi.inject.Injector;
import com.ulfric.commons.collect.MapUtils;

public class SharedScopeStrategy implements ScopeStrategy<Shared> {

	private final Map<Class<?>, Object> cache = MapUtils.newSynchronizedIdentityHashMap();

	@Override
	public <T> T getInstance(Class<T> request, Shared scope, Injector injector)
	{
		@SuppressWarnings("unchecked")
		T value = (T) this.cache.computeIfAbsent(request, type -> this.createInstance(request, injector));
		return value;
	}

	private <T> T createInstance(Class<T> request, Injector injector)
	{
		T instance = InstanceUtils.getInstance(request);

		injector.injectState(instance);

		return instance;
	}

}