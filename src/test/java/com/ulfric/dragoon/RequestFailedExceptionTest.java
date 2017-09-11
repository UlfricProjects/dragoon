package com.ulfric.dragoon;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import java.util.Arrays;

class RequestFailedExceptionTest {

	@Test
	void testWithArguments() {
		Object[] array = new Object[] {1, "Hello", 2, 3};
		Parameters parameters = Parameters.unqualified(array);
		Truth.assertThat(new RequestFailedException(Object.class, parameters, null).getMessage())
		        .contains(Arrays.toString(array));
	}

}
