package com.ulfric.dragoon.container;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(JUnitPlatform.class)
public class MultiFeatureTest {

	@Mock
	private Feature feature;

	private Feature multi;

	@BeforeEach
	void setup()
	{
		MockitoAnnotations.initMocks(this);
		this.multi = new MultiFeature(null, Collections.singletonList(this.feature));
	}

	@Test
	void testEnable()
	{
		Mockito.verifyZeroInteractions(this.feature);
		this.multi.enable();
		Mockito.verify(this.feature, Mockito.times(1)).enable();
	}

	@Test
	void testDisable()
	{
		Mockito.verifyZeroInteractions(this.feature);
		this.multi.enable();
		this.multi.disable();
		Mockito.verify(this.feature, Mockito.times(1)).disable();
	}

}