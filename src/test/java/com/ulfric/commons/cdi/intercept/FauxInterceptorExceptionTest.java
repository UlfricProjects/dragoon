package com.ulfric.commons.cdi.intercept;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class FauxInterceptorExceptionTest {

	@Test
	void testNew_null()
	{
		Verify.that(new FauxInterceptorException(null).getMessage()).isEqualTo("null");
	}

	@Test
	void testNew_Integer5()
	{
		Verify.that(new FauxInterceptorException(5).getMessage()).isEqualTo("5");
	}

}