package com.ulfric.commons.cdi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Scoped;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class InjectorTest {

	private ObjectFactory factory;
	private Injector injector;

	@BeforeEach
	void init()
	{
		this.factory = ObjectFactory.newInstance();
		this.injector = new Injector(this.factory);
	}

	@Test
	void testInjectFields_isRead_isNotInjected()
	{
		Scoped<Example> scoped = new Scoped<>(new Example());
		scoped.readOrThrow();
		this.injector.injectFields(scoped);
		Verify.that(scoped.readOrThrow().value).isNull();
	}

	@Test
	void testInjectFields_notIsRead_isInjected()
	{
		Scoped<Example> scoped = new Scoped<>(new Example());
		this.injector.injectFields(scoped);
		Verify.that(scoped.readOrThrow().value).isNotNull();
	}

	@Test
	void testInjectFields_empty()
	{
		Scoped<Example> scoped = new Scoped<>(null);
		Verify.that(() -> this.injector.injectFields(scoped)).doesThrow(IllegalStateException.class);
	}

	static final class Example
	{
		@Inject
		Object value;
	}

}