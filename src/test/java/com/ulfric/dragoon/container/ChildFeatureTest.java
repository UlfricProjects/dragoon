package com.ulfric.dragoon.container;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ChildFeatureTest {

	@Test
	void testNew()
	{
		Verify.that(() -> new ChildFeature(null)).runsWithoutExceptions();
	}

}