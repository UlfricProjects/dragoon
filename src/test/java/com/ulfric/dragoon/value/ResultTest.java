package com.ulfric.dragoon.value;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

class ResultTest {

	@Test
	void testSuccessIsTrue() {
		Truth.assertThat(Result.SUCCESS.isSuccess()).isTrue();
	}

	@Test
	void testFailureIsFalse() {
		Truth.assertThat(Result.FAILURE.isSuccess()).isFalse();
	}

}
