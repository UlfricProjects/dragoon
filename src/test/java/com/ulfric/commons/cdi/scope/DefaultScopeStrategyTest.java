package com.ulfric.commons.cdi.scope;

import com.ulfric.verify.Verify;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class DefaultScopeStrategyTest {

	@Test
	void testGetOrCreate_null_throwsNPE()
	{
		Verify.that(() -> DefaultScopeStrategy.INSTANCE.getOrCreate(null)).doesThrow(NullPointerException.class);
	}

	@Test
	void testGetOrCreate_nonnull_nonnull()
	{
		Verify.that(DefaultScopeStrategy.INSTANCE.getOrCreate(Example.class)).isNotNull();
	}

	private static final class Example
	{
		
	}

}