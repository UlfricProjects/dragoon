package com.ulfric.commons.cdi.intercept.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Interceptor;

public class AsynchronousInterceptor implements Interceptor<Future<?>> {

	private final ExecutorService service = Executors.newCachedThreadPool();

	@Override
	public Future<?> intercept(Context<Future<?>> context)
	{
		return this.service.submit(() ->
		{
			Future<?> future = context.proceed();

			return future == null ? null : future.get();
		});
	}

}
