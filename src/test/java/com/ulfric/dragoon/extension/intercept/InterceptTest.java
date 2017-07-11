package com.ulfric.dragoon.extension.intercept;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.ObjectFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Callable;

@RunWith(JUnitPlatform.class)
class InterceptTest {

	private ObjectFactory factory;

	@BeforeEach
	void setup() {
		this.factory = new ObjectFactory();
	}

	@Test
	void testInterceptorWithoutRegistration() {
		boolean[] hello = new boolean[2];
		this.factory.request(Intercepted.class).hello(hello);
		Truth.assertThat(hello).asList().containsExactly(true, false);
	}

	@Test
	void testInterceptor() {
		this.factory.bind(InterceptMe.class).to(InterceptMeInterceptor.class);

		boolean[] hello = new boolean[2];
		this.factory.request(Intercepted.class).hello(hello);
		Truth.assertThat(hello).asList().containsExactly(true, true);
	}

	@Intercept
	@Retention(RetentionPolicy.RUNTIME)
	@interface InterceptMe {
	}

	static class InterceptMeInterceptor extends Interceptor<InterceptMe> {
		public InterceptMeInterceptor(InterceptMe declaration) {
			super(declaration);
		}

		@Override
		public Object invoke(Object[] arguments, Callable<?> proceed) throws Exception {
			boolean[] set = (boolean[]) arguments[0];
			set[1] = true;
			return proceed.call();
		}
	}

	static class Intercepted {
		@InterceptMe
		void hello(boolean[] set) {
			set[0] = true;
		}
	}

}
