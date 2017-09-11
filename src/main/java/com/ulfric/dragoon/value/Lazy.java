package com.ulfric.dragoon.value;

import java.util.Objects;
import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {

	public static <T> Lazy<T> of(Supplier<T> supplier) {
		Objects.requireNonNull(supplier, "supplier");

		return new Lazy<>(supplier, false);
	}

	public static <T> Lazy<T> ofRetrying(Supplier<T> supplier) {
		Objects.requireNonNull(supplier, "supplier");

		return new Lazy<>(supplier, true);
	}

	private final Supplier<T> supplier;
	private final boolean retry;
	private T value;
	private boolean called;

	private Lazy(Supplier<T> supplier, boolean retry) {
		this.supplier = supplier;
		this.retry = retry;
	}

	@Override
	public T get() {
		if (called) {
			if (shouldRetry()) {
				return value;
			}
		}

		called = true;
		value = supplier.get();
		return value;
	}

	private boolean shouldRetry() {
		return this.retry && this.value == null;
	}

}
