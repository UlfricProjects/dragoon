package com.ulfric.commons.cdi.construct.scope;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class DefaultScopeStrategyTest {

	@Test
	public void test_enum_jargon()
	{
		Verify.that(() -> DefaultScopeStrategy.valueOf(DefaultScopeStrategy.INSTANCE.name())).runsWithoutExceptions();
	}

}
