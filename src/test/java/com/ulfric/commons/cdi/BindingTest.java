package com.ulfric.commons.cdi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class BindingTest {

	private Bindings bindings;

	@BeforeEach
	void init()
	{
		this.bindings = new Bindings();
	}

	@Test
	void testNew_null()
	{
		Verify.that(() -> this.bind(null)).runsWithoutExceptions();
	}

	@Test
	void testTo_null()
	{
		Verify.that(() -> this.bind(Hello.class).to(HelloImpl.class)).runsWithoutExceptions();
	}

	private Binding bind(Class<?> binding)
	{
		return new Binding(this.bindings, binding);
	}

	interface Hello
	{
		
	}

	class HelloImpl implements Hello
	{
		
	}

}