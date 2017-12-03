package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.lang.reflect.Executable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.Interceptor;

public class AsynchronousInterceptor extends Interceptor<Asynchronous> {

	public static ExecutorService executor(Factory factory, Asynchronous asynchronous) {
		return executor(factory, asynchronous.value());
	}

	public static ExecutorService executor(Factory factory,
			Class<? extends Supplier<? extends ExecutorService>> supplier) {
		return factory.request(supplier).get();
	}

	@Inject
	private ObjectFactory factory;

	private ExecutorService executor;

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

	private ExecutorService executor() {
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
