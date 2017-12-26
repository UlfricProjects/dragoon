package com.ulfric.dragoon.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

class FeatureTest {

	private Feature feature;

	@BeforeEach
	void setup() {
		feature = Mockito.mock(Feature.class);
		Feature.register(feature);
	}

	@AfterEach
	void teardown() {
		Feature.unregister(feature);
	}

	@Test
	void testWrapApplication() {
		Application expected = new Application();
		Truth.assertThat(Feature.wrap(expected)).isInstanceOf(AggregateApplication.class);
	}

	@Test
	void testDoNothingWrapper() {
		Mockito.when(feature.apply(ArgumentMatchers.any())).thenReturn(null);
		Truth.assertThat(Feature.wrap(new Object())).isNull();
	}

	@Test
	void testNewApplicationWrapper() {
		Object expected = new Object();
		Mockito.when(feature.apply(ArgumentMatchers.any())).thenReturn(new Application());
		Truth.assertThat(Feature.wrap(expected)).isInstanceOf(AggregateApplication.class);
		Mockito.verify(feature, Mockito.times(1)).apply(expected);
	}

}
