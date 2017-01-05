package com.ulfric.commons.cdi.intercept.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Interceptor;

public class AsynchronousInterceptor implements Interceptor {

	private final Executor service = Executors.newCachedThreadPool();

	@Override
	public Future<?> intercept(Context context)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			Object future = context.proceed();

			try
			{
				return future instanceof Future ? ((Future<?>) future).get() : null;
			}
			catch (InterruptedException | ExecutionException caught)
			{
				return ExceptionUtils.rethrow(caught);
			}
		}, this.service);
	}

}
