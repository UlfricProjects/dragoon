package com.ulfric.dragoon.container;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class FeatureWrapperMissingExceptionTest {

	@Test
	void testException_construction()
	{
		Verify.that(() -> new FeatureWrapperMissingException(null)).runsWithoutExceptions();
		Verify.that(() ->
		{
			throw new FeatureWrapperMissingException(Object.class);
		}).doesThrow(FeatureWrapperMissingException.class);
	}

}
