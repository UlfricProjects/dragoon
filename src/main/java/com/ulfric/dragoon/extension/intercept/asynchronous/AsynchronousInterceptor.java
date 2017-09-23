package com.ulfric.dragoon.extension.intercept.asynchronous;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.Interceptor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AsynchronousInterceptor extends Interceptor<Asynchronous> {

	@Inject
	private ObjectFactory factory;

	private ExecutorService executor;

	public AsynchronousInterceptor(Asynchronous declaration) {
		super(declaration);
	}

	@Override
	public Future<?> invoke(Object[] arguments, Callable<?> proceed) throws Exception {
		return executor().submit(unwrapAsynchronousResult(proceed));
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
