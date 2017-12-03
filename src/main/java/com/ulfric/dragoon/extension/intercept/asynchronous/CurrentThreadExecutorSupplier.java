package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.util.function.Supplier;

public enum CurrentThreadExecutorSupplier implements Supplier<CurrentThreadExecutor> {

	INSTANCE;

	private final CurrentThreadExecutor executor = new CurrentThreadExecutor();

	@Override
	public CurrentThreadExecutor get() {
		return executor;
	}

}
