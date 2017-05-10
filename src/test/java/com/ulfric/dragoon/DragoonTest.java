package com.ulfric.dragoon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.Intercept;
import com.ulfric.dragoon.extension.intercept.Interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

@RunWith(JUnitPlatform.class)
@DisplayName("Dragoon Acceptance Tests")
class DragoonTest {

	@Test
	@DisplayName("Test everything works")
	void testDragoon()
	{
		ObjectFactory factory = ObjectFactory.newInstance();
		factory.bind(Throw.class).to(ThrowInterceptor.class);

		SampleRequest instance = factory.request(SampleRequest.class);
		Assertions.assertNotNull(instance.value);

		try
		{
			instance.interceptMe();
			Assertions.fail("Expected exception");
		}
		catch(NoSuchElementException expected)
		{
			
		}
	}

	public static class SampleRequest
	{
		@Inject
		public Object value;

		@Throw(NoSuchElementException.class)
		public void interceptMe()
		{
			
		}
	}

	@Intercept
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Throw
	{
		Class<? extends RuntimeException> value();
	}

	public static class ThrowInterceptor extends Interceptor<Throw>
	{
		public ThrowInterceptor(Throw declaration)
		{
			super(declaration);
		}

		@Override
		public Object invoke(Object[] arguments, Callable<?> proceed) throws Exception
		{
			throw this.getDeclaration().value().newInstance();
		}
	}

}