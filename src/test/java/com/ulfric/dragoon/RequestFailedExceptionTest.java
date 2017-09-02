package com.ulfric.dragoon;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import java.util.Arrays;

class RequestFailedExceptionTest {

	@Test
	void testWithArguments() {
		Object[] array = new Object[] {1, "Hello", 2, 3};
		Truth.assertThat(new RequestFailedException(Object.class, array, null).getMessage())
		        .contains(Arrays.toString(array));
	}

	@Test
	void testWithoutArguments() {
		Object[] array = new Object[0];
		Truth.assertThat(new RequestFailedException(Object.class, array, null).getMessage())
		        .doesNotContain(Arrays.toString(array));
	}

}
