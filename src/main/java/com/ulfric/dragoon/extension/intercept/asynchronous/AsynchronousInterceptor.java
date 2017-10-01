package com.ulfric.dragoon.extension.intercept.asynchronous;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.Interceptor;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class AsynchronousInterceptor extends Interceptor<Asynchronous> {

	@Inject
	private ObjectFactory factory;

	private ExecutorService executor;

	public AsynchronousInterceptor(Asynchronous declaration) {
		super(declaration);
	}

	@Override
	public CompletableFuture<?> invoke(Object[] arguments, Callable<?> proceed) throws Exception {
		return CompletableFuture.supplyAsync(unwrapAsynchronousResult(proceed), executor());
	}

	private Supplier<?> unwrapAsynchronousResult(Callable<?> callable) {
		return () -> {
			Object value;
			try {
				value = callable.call();
			} catch (Exception exception) {
				return null;
			}

			if (value instanceof Future) {
				try {
					return ((Future<?>) value).get();
				} catch (InterruptedException | ExecutionException exception) {
					return null;
				}
			}

			return value;
		};
	}

	private ExecutorService executor() {
		if (executor != null) {
			return executor;
		}

		synchronized (this) {
			if (executor != null) {
				return executor;
			}

			executor = factory.request(declaration.executor()).get();
		}

		return executor;
	}

}
