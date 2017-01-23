package com.ulfric.commons.cdi.scope;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ScopeNotPresentExceptionTest {

	@Test
	void testNew_nonnull_isExpectedMessage()
	{
		Verify.that(new ScopeNotPresentException(Object.class).getMessage())
			.isEqualTo("Tried to use scope: " + Object.class);
	}

}