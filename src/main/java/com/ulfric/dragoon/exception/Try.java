package com.ulfric.dragoon.exception;

public class Try {

	public static void to(CheckedRunnable runnable) {
		try {
			runnable.run();
		} catch (Throwable rethrow) {
			throw new RuntimeException(rethrow);
		}
	}

	public static <T> T to(CheckedSupplier<T> supplier) {
		try {
			return supplier.get();
		} catch (Throwable rethrow) {
			throw new RuntimeException(rethrow);
		}
	}

	@FunctionalInterface
	public interface CheckedRunnable {
		void run() throws Throwable;
	}

	@FunctionalInterface
	public interface CheckedSupplier<T> {
		T get() throws Throwable;
	}

	private Try() {}

}
