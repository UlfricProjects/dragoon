package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.Interceptor;

public class AsynchronousInterceptor extends Interceptor<Asynchronous> {

	@Inject
	private ObjectFactory factory;

	private ForkJoinPool pool;

	public AsynchronousInterceptor(Asynchronous declaration) {
		super(declaration);
	}

	@Override
	public Future<?> invoke(Object[] arguments, Callable<?> proceed) throws Exception {
		return pool().submit(unwrapAsynchronousResult(proceed));
	}

	private Callable<?> unwrapAsynchronousResult(Callable<?> callable) {
		return () -> {
			Object value = callable.call();

			if (value instanceof AsynchronousResult) {
				return ((AsynchronousResult<?>) value).get();
			}

			return value;
		};
	}

	private ForkJoinPool pool() {
		if (pool != null) {
			return pool;
		}

		synchronized (this) {
			if (pool != null) {
				return pool;
			}

			pool = factory.request(declaration.forkJoinPool()).get();
		}

		return pool;
	}

}
