package com.ulfric.commons.cdi;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class DynamicImplementationFactoryTest {

	private final ImplementationFactory factory = new DynamicImplementationFactory();

	@Test
	void testCreateImplementationClass_concrete_nonnull()
	{
		Verify.that(this.factory.createImplementationClass(Hello.class)).isNotNull();
	}

	@Test
	void testCreateImplementationClass_interface_null()
	{
		Verify.that(this.factory.createImplementationClass(IHello.class)).isNull();
	}

	@Test
	void testCreateImplementationClass_abstract_null()
	{
		Verify.that(this.factory.createImplementationClass(AHello.class)).isNull();
	}

	interface IHello
	{
		
	}

	static abstract class AHello implements IHello
	{
		
	}

	static class Hello extends AHello
	{
		
	}

}