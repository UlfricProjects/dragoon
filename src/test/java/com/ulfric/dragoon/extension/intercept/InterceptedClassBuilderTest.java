package com.ulfric.dragoon.extension.intercept;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Executable;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;
import com.ulfric.dragoon.DragoonTestSuite;

class InterceptedClassBuilderTest extends DragoonTestSuite {

	@Test
	void testBuildWithoutInterceptors() {
		Truth.assertThat(build(Object.class)).isSameAs(Object.class);
	}

	@Test
	void testBuildWithInterceptorsButNotBound() {
		Truth.assertThat(build(Intercepted.class)).isSameAs(Intercepted.class);
	}

	@Test
	void testBuildWithInterceptors() {
		factory.bind(Example.class).to(ExampleInterceptor.class);
		Truth.assertThat(build(Intercepted.class)).isNotSameAs(Intercepted.class);
	}

	private <T> Class<? extends T> build(Class<T> type) {
		return new InterceptedClassBuilder<>(factory, type).build();
	}

	@Intercept
	@Retention(RetentionPolicy.RUNTIME)
	@interface Example {
	}

	static class ExampleInterceptor extends Interceptor<Example> {
		public ExampleInterceptor(Executable call, Example declaration) {
			super(call, declaration);
		}

		@Override
		public Object invoke(Object[] arguments, Callable<?> proceed) throws Exception {
			return proceed.call();
		}
	}

	static class Intercepted {
		@Example
		void hello() {}
	}

}
