package com.ulfric.dragoon.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.UtilityTest;
import com.ulfric.dragoon.exception.Try.CheckedRunnable;

class TryTest extends UtilityTest {

	public TryTest() {
		super(Try.class);
	}

	@Test
	void testToRunnableRuns() {
		boolean[] ran = new boolean[1];
		Try.to(() -> ran[0] = true);
		Truth.assertThat(ran[0]).isTrue();
	}

	@Test
	void testToRunnableRethrows() {
		Assertions.assertThrows(RuntimeException.class, () -> Try.to((CheckedRunnable) () -> {
			throw new Exception();
		}));
	}

	@Test
	void testToSupplierRuns() {
		Truth.assertThat(Try.to(() -> true)).isTrue();
	}

	@Test
	void testToSupplierRethrows() {
		Assertions.assertThrows(RuntimeException.class, () -> Try.to(() -> {
			throw new Exception();
		}));
	}

}
