package com.ulfric.dragoon.interceptors;

import org.apache.commons.lang3.ObjectUtils;

import com.ulfric.dragoon.intercept.Context;
import com.ulfric.dragoon.intercept.Interceptor;

public final class InitializeInterceptor implements Interceptor {

	private volatile boolean initialized;

	@Override
	public synchronized Object intercept(Context invocation)
	{
		if (this.initialized)
		{
			throw new IllegalStateException("Already initialized " +
					ObjectUtils.identityToString(invocation.getOwner()));
		}

		this.initialized = true;
		return invocation.proceed();
	}

}