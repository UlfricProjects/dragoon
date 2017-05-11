package com.ulfric.dragoon.extension.intercept;

import com.google.common.truth.Truth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.ObjectFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Callable;

@RunWith(JUnitPlatform.class)
class InterceptedClassBuilderTest {

	private ObjectFactory factory;

	@BeforeEach
	void setup()
	{
		this.factory = new ObjectFactory();
	}

	@Test
	void testBuildWithoutInterceptors()
	{
		Truth.assertThat(this.build(Object.class)).isSameAs(Object.class);
	}

	@Test
	void testBuildWithInterceptorsButNotBound()
	{
		Truth.assertThat(this.build(Intercepted.class)).isSameAs(Intercepted.class);
	}

	@Test
	void testBuildWithInterceptors()
	{
		this.factory.bind(Example.class).to(ExampleInterceptor.class);
		Truth.assertThat(this.build(Intercepted.class)).isNotSameAs(Intercepted.class);
	}

	private <T> Class<? extends T> build(Class<T> type)
	{
		return new InterceptedClassBuilder<>(this.factory, type).build();
	}

	@Intercept
	@Retention(RetentionPolicy.RUNTIME)
	@interface Example { }

	static class ExampleInterceptor extends Interceptor<Example>
	{
		public ExampleInterceptor(Example declaration)
		{
			super(declaration);
		}

		@Override
		public Object invoke(Object[] arguments, Callable<?> proceed) throws Exception
		{
			return proceed.call();
		}
	}

	static class Intercepted
	{
		@Example
		void hello() { }
	}

}