package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AsynchronousResult<V> implements Future<V> {

	private static final AsynchronousResult<?> NULL = new AsynchronousResult<>(null);

	public static final AsynchronousResult<Boolean> TRUE = new AsynchronousResult<>(true);
	public static final AsynchronousResult<Boolean> FALSE = new AsynchronousResult<>(false);

	public static <V> AsynchronousResult<V> of(V value) {
		if (value == null) {
			return ofNull();
		}

		return new AsynchronousResult<>(value);
	}

	@SuppressWarnings("unchecked")
	public static <V> AsynchronousResult<V> ofNull() {
		return (AsynchronousResult<V>) NULL;
	}

	private final V value;

	private AsynchronousResult(V value) {
		this.value = value;
	}

	@Override
	public V get() {
		return value;
	}

	@Override
	public V get(long timeout, TimeUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCancelled() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDone() {
		throw new UnsupportedOperationException();
	}

}
