package com.ulfric.commons.cdi.interceptors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.commons.exception.Try;

public final class AsynchronousInterceptor implements Interceptor {

	private final Executor service = Executors.newCachedThreadPool();

	@Override
	public Object intercept(Context context)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			Object future = context.proceed();
			return future instanceof Future ? Try.to(((Future<?>) future)) : null;
		}, this.service);
	}

}