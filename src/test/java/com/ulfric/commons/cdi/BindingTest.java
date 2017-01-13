package com.ulfric.commons.cdi;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class BindingTest {

	@Test
	void testNew()
	{
		Verify.that(() -> new Binding<>(null)).runsWithoutExceptions();
	}

}