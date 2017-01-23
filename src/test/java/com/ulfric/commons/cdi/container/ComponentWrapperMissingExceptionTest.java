package com.ulfric.commons.cdi.container;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ComponentWrapperMissingExceptionTest {

	@Test
	void testException_construction()
	{
		Verify.that(() -> new ComponentWrapperMissingException(null)).runsWithoutExceptions();
		Verify.that(() ->
		{
			throw new ComponentWrapperMissingException(Object.class);
		}).doesThrow(ComponentWrapperMissingException.class);
	}

}
