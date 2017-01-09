package com.ulfric.commons.cdi.construct;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class IllegalRequestExactExceptionTest {

	@Test
	void testNew_NullRequestNullValue()
	{
		Verify.that(new IllegalRequestExactException(null, null).getMessage())
			.isEqualTo("Attempted to request exact type null, value was of type null");
	}

	@Test
	void testNew_ObjectRequestNullValue()
	{
		Verify.that(new IllegalRequestExactException(Object.class, null).getMessage())
			.isEqualTo("Attempted to request exact type class java.lang.Object, value was of type null");
	}

	@Test
	void testNew_NullRequestObjectValue()
	{
		Verify.that(new IllegalRequestExactException(null, new Object()).getMessage())
			.isEqualTo("Attempted to request exact type null, value was of type java.lang.Object");
	}
	/*
	public IllegalRequestExactException(Class<?> request, Object value)
	{
		super("Attempted to request exact type " + request + ", value was of type "
				+ (value == null ? "null" : value.getClass().getName()));
	}
	*/

}