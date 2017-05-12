package com.ulfric.dragoon.exception;

import com.google.common.truth.Truth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class TryTest {

	@Test
	void testToRuns()
	{
		boolean[] ran = new boolean[1];
		Try.to(() -> ran[0] = true);
		Truth.assertThat(ran[0]).isTrue();
	}

	@Test
	void testToRethrows()
	{
		Assertions.assertThrows(RuntimeException.class, () -> Try.to(() -> { throw new Exception(); }));
	}

}