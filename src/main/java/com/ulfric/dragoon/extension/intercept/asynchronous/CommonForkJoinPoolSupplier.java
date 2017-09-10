package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

public enum CommonForkJoinPoolSupplier implements Supplier<ForkJoinPool> {

	INSTANCE;

	@Override
	public ForkJoinPool get() {
		return ForkJoinPool.commonPool();
	}

}
