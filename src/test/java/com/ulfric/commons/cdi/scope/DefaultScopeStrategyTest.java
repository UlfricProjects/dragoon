package com.ulfric.commons.cdi.scope;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class DefaultScopeStrategyTest {

	@Test
	void testValueOf_forJacoco()
	{
		DefaultScopeStrategy.valueOf("INSTANCE");
	}

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