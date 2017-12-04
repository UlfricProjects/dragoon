package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.lang.reflect.Executable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.Interceptor;

public class AsynchronousInterceptor extends Interceptor<Asynchronous> {

	public static Executor executor(Factory factory, Asynchronous asynchronous) {
		return executor(factory, asynchronous.value());
	}

	public static Executor executor(Factory factory,
			Class<? extends Supplier<? extends Executor>> supplier) {
		return factory.request(supplier).get();
	}

	@Inject
	private ObjectFactory factory;

	private Executor executor;

	public AsynchronousInterceptor(Executable call, Asynchronous declaration) {
		super(call, declaration);
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

	private Executor executor() {
		if (executor != null) {
			return executor;
		}

		synchronized (this) {
			if (executor != null) {
				return executor;
			}

			executor = executor(factory, declaration);
		}

		return executor;
	}

}
