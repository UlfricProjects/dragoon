package com.ulfric.commons.cdi;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class BindingsTest {

	@Test
	void testNew()
	{
		Verify.that(Bindings::new).runsWithoutExceptions();
	}

}