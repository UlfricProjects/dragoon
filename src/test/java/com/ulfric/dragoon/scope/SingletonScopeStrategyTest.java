package com.ulfric.dragoon.scope;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class SingletonScopeStrategyTest {

	@Test
	void testGetOrCreate_empty()
	{
		SingletonScopeStrategy strategy = new SingletonScopeStrategy();
		Verify.that(strategy.getOrCreate(null)).isNull();
	}

	@Test
	void testGetOrEmpty_empty()
	{
		SingletonScopeStrategy strategy = new SingletonScopeStrategy();
		Verify.that(strategy.getOrEmpty(null)).isNull();
	}

	@Test
	void testGetOrCreate()
	{
		SingletonScopeStrategy strategy = new SingletonScopeStrategy();
		Object value = new Object();
		strategy.setInstance(value);
		Verify.that(strategy.getOrCreate(null).read()).isSameAs(value);
	}

	@Test
	void testGetOrEmpty()
	{
		SingletonScopeStrategy strategy = new SingletonScopeStrategy();
		Object value = new Object();
		strategy.setInstance(value);
		Verify.that(strategy.getOrEmpty(null).read()).isSameAs(value);
	}

}