package com.ulfric.commons.cdi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class BindingsTest {

	private Bindings bindings;

	@BeforeEach
	void init()
	{
		this.bindings = new Bindings();
	}

	@Test
	void testHasParent_root()
	{
		Verify.that(this.bindings.hasParent()).isFalse();
	}

	@Test
	void testHasParent_child()
	{
		Verify.that(new Bindings(this.bindings).hasParent()).isTrue();
	}

	@Test
	void testGetRegisteredBinding_empty_isNull()
	{
		Verify.that(this.getBinding(Hello.class)).isNull();
	}

	@Test
	void testRegisterBinding_HelloToHelloImpl_IsBound()
	{
		Verify.that(this.getBinding(Hello.class)).isNull();
		this.bindings.registerBinding(Hello.class, HelloImpl.class);
		Verify.that(this.getBinding(Hello.class)).isSameAs(HelloImpl.class);
	}

	private Class<?> getBinding(Class<?> request)
	{
		return this.bindings.getOrTryToCreateBinding(request, ignore -> { return null; });
	}

	interface Hello
	{
		
	}

	class HelloImpl implements Hello
	{
		
	}

}