package com.ulfric.dragoon;

import com.google.common.truth.Truth;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(JUnitPlatform.class)
class RequestFailedExceptionTest {

	@Test
	void testWithArguments()
	{
		Object[] array = new Object[] { 1, "Hello", 2, 3 };
		Truth.assertThat(new RequestFailedException(Object.class, array, null).getMessage()).contains(Arrays.toString(array));
	}

	@Test
	void testWithoutArguments()
	{
		Object[] array = new Object[0];
		Truth.assertThat(new RequestFailedException(Object.class, array, null).getMessage()).doesNotContain(Arrays.toString(array));
	}

}