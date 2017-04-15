package com.ulfric.dragoon.interceptors;

import java.util.Map;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.intercept.Context;
import com.ulfric.dragoon.intercept.Interceptor;

public final class CacheInterceptor implements Interceptor {

	@Inject
	private ObjectFactory factory;

	private Map<Object, Object> cache;

	@Override
	public Object intercept(Context context)
	{
		if (this.cache == null)
		{
			Cache cacheDescription = context.getDestinationExecutable().getAnnotation(Cache.class);
			this.cache = this.factory.requestExact(cacheDescription.value());
		}

		Object[] arguments = context.getArguments();
		Object key = arguments.length == 0 ? null : arguments[0];

		return this.cache.computeIfAbsent(key, ignore -> context.proceed());
	}

}