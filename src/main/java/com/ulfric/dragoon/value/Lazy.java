package com.ulfric.dragoon.value;

import java.util.Objects;
import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {

	public static <T> Lazy<T> of(Supplier<T> supplier) {
		Objects.requireNonNull(supplier, "supplier");

		return new Lazy<>(supplier);
	}

	private final Supplier<T> supplier;
	private T value;
	private boolean called;

	private Lazy(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@Override
	public T get() {
		if (this.called) {
			return this.value;
		}

		this.called = true;
		this.value = this.supplier.get();
		return this.value;
	}

}
