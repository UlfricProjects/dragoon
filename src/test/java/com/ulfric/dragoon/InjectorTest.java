package com.ulfric.dragoon;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.scope.Scoped;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class InjectorTest {

	private ObjectFactory factory;
	private Injector injector;

	@BeforeEach
	void init()
	{
		this.factory = TestObjectFactory.newInstance();
		this.injector = new Injector(this.factory);
	}

	@Test
	void testInjectFields_isRead_isNotInjected()
	{
		Scoped<Example> scoped = new Scoped<>(Example.class, new Example());
		scoped.read("inject");
		this.doInjection(scoped);
		Verify.that(scoped.read("inject").value).isNull();
	}

	@Test
	void testInjectFields_notIsRead_isInjected()
	{
		Scoped<Example> scoped = new Scoped<>(Example.class, new Example());
		this.doInjection(scoped);
		Verify.that(scoped.read("inject").value).isNotNull();
	}

	@Test
	void testInjectFields_empty()
	{
		Scoped<Example> scoped = new Scoped<>(Example.class, null);
		Verify.that(() -> this.doInjection(scoped)).doesThrow(NoSuchElementException.class);
	}

	private void doInjection(Scoped<?> scoped)
	{
		this.injector.getInjections(scoped).forEachRemaining(Runnable::run);
	}

	static final class Example
	{
		@Inject
		Object value;

		@Inject
		Obj otherValue;
	}

	static final class Obj
	{
		
	}

}