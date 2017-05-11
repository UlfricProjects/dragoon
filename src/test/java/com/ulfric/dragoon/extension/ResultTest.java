package com.ulfric.dragoon.extension;

import com.google.common.truth.Truth;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class ResultTest {

	@Test
	void testSuccessIsTrue()
	{
		Truth.assertThat(Result.SUCCESS.isSuccess()).isTrue();
	}

	@Test
	void testFailureIsFalse()
	{
		Truth.assertThat(Result.FAILURE.isSuccess()).isFalse();
	}

}