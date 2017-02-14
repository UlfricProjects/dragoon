package com.ulfric.commons.cdi.scope;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class SharedScopeStrategyTest {

	private ScopeStrategy scope;

	@BeforeEach
	void init()
	{
		this.scope = new SharedScopeStrategy();
	}

	@Test
	void testGetOrCreate_nonnull_returnsSameValue()
	{
		Verify.that(() -> this.scope.getOrCreate(Object.class).readOrThrow()).suppliesNonUniqueValues();
	}

}