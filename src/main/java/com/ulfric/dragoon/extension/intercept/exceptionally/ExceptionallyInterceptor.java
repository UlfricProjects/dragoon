package com.ulfric.dragoon.extension.intercept.exceptionally;

import java.lang.reflect.Executable;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.Interceptor;
import com.ulfric.dragoon.extension.postconstruct.PostConstruct;

public class ExceptionallyInterceptor extends Interceptor<Exceptionally> {

	private Consumer<Throwable> exceptionHandler;

	@Inject
	private Factory factory;

	public ExceptionallyInterceptor(Executable call, Exceptionally declaration) {
		super(call, declaration);
	}

	@PostConstruct
	public void initializeExceptionHandler() {
		this.exceptionHandler = factory.request(this.declaration.value());
	}

	@Override
	public Object invoke(Object[] arguments, Callable<?> proceed) throws Exception {
		try {
			return proceed.call();
		} catch (Throwable thrown) {
			exceptionHandler.accept(thrown);
			throw thrown;
		}
	}

}
