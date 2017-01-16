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

	@Test
	void testCreateImplementationClass_concrete_notSameAsParent()
	{
		Verify.that(this.factory.createImplementationClass(Hello.class)).isNotSameAs(Hello.class);
	}

	@Test
	void testCreateImplementationClass_concrete_extendedFromParent()
	{
		Verify.that(this.factory.createImplementationClass(Hello.class)).isAssignableTo(Hello.class);
	}

	@Test
	void testCreateImplementationClass_final_same()
	{
		Verify.that(this.factory.createImplementationClass(FHello.class)).isSameAs(FHello.class);
	}

	@Test
	void testCreateImplementationClass_primitive_same()
	{
		Class<?> primitive = int.class;
		Verify.that(this.factory.createImplementationClass(primitive)).isNull();
	}

	@Test
	void testCreateImplementationClass_array_same()
	{
		Class<?> array = Object[].class;
		Verify.that(this.factory.createImplementationClass(array)).isNull();
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

	final static class FHello extends Hello
	{
		
	}

}