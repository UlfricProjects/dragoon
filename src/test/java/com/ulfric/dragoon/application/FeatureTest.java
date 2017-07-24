package com.ulfric.dragoon.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

@RunWith(JUnitPlatform.class)
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
		Truth.assertThat(Feature.wrap(expected)).isSameAs(expected);
	}

	@Test
	void testDoNothingWrapper() {
		Mockito.when(feature.apply(Matchers.any())).thenReturn(null);
		Truth.assertThat(Feature.wrap(new Object())).isNull();
	}

	@Test
	void testNewApplicationWrapper() {
		Application expected = new Application();
		Mockito.when(feature.apply(Matchers.any())).thenReturn(expected);
		Truth.assertThat(Feature.wrap(new Object())).isSameAs(expected);
	}

}
