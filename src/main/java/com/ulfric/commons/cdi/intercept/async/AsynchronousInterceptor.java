package com.ulfric.commons.cdi.intercept.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Interceptor;

public class AsynchronousInterceptor implements Interceptor<Future<?>> {

	private final Executor service = Executors.newCachedThreadPool();

	@Override
	public Future<?> intercept(Context<Future<?>> context)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			Future<?> future = context.proceed();

			try
			{
				return future == null ? null : future.get();
			}
			catch (InterruptedException | ExecutionException e)
			{
				throw new RuntimeException(e);
			}
		}, this.service);
	}

}
