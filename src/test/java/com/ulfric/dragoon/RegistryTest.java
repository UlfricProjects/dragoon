package com.ulfric.dragoon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class RegistryTest {

	private Registry<?, ?> bindings;

	@BeforeEach
	void init()
	{
		this.bindings = new RegistryImpl();
	}

	@Test
	void testHasParent_root()
	{
		Verify.that(this.bindings.hasParent()).isFalse();
	}

	@Test
	void testHasParent_child()
	{
		Verify.that(this.bindings.createChild().hasParent()).isTrue();
	}

	@Test
	void testGetRegisteredBinding_empty_isNull()
	{
		Verify.that(this.bindings.getRegisteredBinding(Hello.class)).isNull();
	}

	@Test
	void testRegisterBinding_HelloToHelloImpl_IsBound()
	{
		Verify.that(this.bindings.getRegisteredBinding(Hello.class)).isNull();
		this.bindings.registerBinding(Hello.class, HelloImpl.class);
		Verify.that(this.bindings.getRegisteredBinding(Hello.class)).isSameAs(HelloImpl.class);
	}

	static final class RegistryImpl extends Registry<RegistryImpl, Class<?>>
	{
		RegistryImpl()
		{
			
		}

		RegistryImpl(RegistryImpl parent)
		{
			super(parent);
		}

		@Override
		void registerBinding(Class<?> request, Class<?> implementation)
		{
			this.registered.put(request, implementation);
		}
	}

	interface Hello
	{
		
	}

	class HelloImpl implements Hello
	{
		
	}

}