package com.ulfric.commons.cdi.scope;

import com.ulfric.verify.Verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class SharedScopeStrategyTest {

	private ScopeStrategy scope;

	@BeforeEach
	void init()
	{
		this.scope = new SharedScopeStrategy();
	}
	
	@Test
	public void testParentScope() {
	    this.scope = new SharedScopeStrategy();
	    this.scope.setParent(scope.getParent());
	}
	
	@Test
	void testGetOrCreate_nonnull_returnsSameValue()
	{
		Verify.that(() -> this.scope.getOrCreate(Object.class).read()).suppliesNonUniqueValues();
	}

}