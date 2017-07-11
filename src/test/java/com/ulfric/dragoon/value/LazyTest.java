package com.ulfric.dragoon.value;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

import java.util.UUID;
import java.util.function.Supplier;

@RunWith(JUnitPlatform.class)
class LazyTest {

	private Object value;

	@BeforeEach
	void generateValue() {
		value = UUID.randomUUID();
	}

	@Test
	void testGetSuppliesCorrectValue() {
		Truth.assertThat(Lazy.of(() -> value).get()).isSameAs(value);
	}

	@Test
	void testLazilyGetsValue() {
		boolean[] ran = new boolean[1];
		Supplier<Object> supplier = () -> {
			ran[0] = true;
			return value;
		};
		Truth.assertThat(ran[0]).isFalse();
		Lazy<Object> lazy = Lazy.of(supplier);
		Truth.assertThat(ran[0]).isFalse();
		Truth.assertThat(lazy.get()).isSameAs(value);
		Truth.assertThat(ran[0]).isTrue();
	}

}
