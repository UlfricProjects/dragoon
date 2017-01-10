package com.ulfric.commons.cdi.construct.scope;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class SuppliedScopeStrategyTest {

	@Test
	public void test_getInstance_returnsValue()
	{
		SuppliedScopeStrategy strategy = new SuppliedScopeStrategy();

		strategy.put(FooClass.class, new FooClass());

		Verify.that(strategy.getInstance(FooClass.class, null, null)).isNotNull();
	}

	public static class FooClass
	{

	}

}