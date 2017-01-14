package com.ulfric.commons.cdi;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class BindingTest {

	@Test
	void testNew_null()
	{
		Verify.that(() -> new Binding<>(null)).runsWithoutExceptions();
	}

	@Test
	void testNew_nonnull()
	{
		Verify.that(() -> new Binding<>(Number.class)).runsWithoutExceptions();
	}

}