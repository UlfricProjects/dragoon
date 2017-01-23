package com.ulfric.commons.cdi.scope;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class SuppliedScopeStrategyTest {

	private SuppliedScopeStrategy strategy;

	@BeforeEach
	void init()
	{
		this.strategy = new SuppliedScopeStrategy();
		this.strategy.register(Registered.class, new RegisteredSupplier());
	}

	@Test
	void testGetOrCreate_registered_returnsValues()
	{
		Verify.that(() -> this.strategy.getOrCreate(Registered.class)).suppliesUniqueValues();
	}

	@Test
	void testGetOrCreate_unregistered_returnsNullScope()
	{
		Verify.that(this.strategy.getOrCreate(Unregistered.class).read()).isNull();
	}

	public static class Unregistered
	{

	}

	public static class Registered
	{

	}

	public static class RegisteredSupplier implements Supplier<Registered>
	{

		@Override
		public Registered get()
		{
			return new Registered();
		}

	}

}
