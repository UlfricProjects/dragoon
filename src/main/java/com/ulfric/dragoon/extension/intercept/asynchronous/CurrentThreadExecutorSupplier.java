package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.util.function.Supplier;

public enum CurrentThreadExecutorSupplier implements Supplier<CurrentThreadExecutor> {

	INSTANCE;

	@Override
	public CurrentThreadExecutor get() {
		return CurrentThreadExecutor.INSTANCE;
	}

}
