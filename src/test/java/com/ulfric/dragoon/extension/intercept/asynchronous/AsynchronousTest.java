package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;
import com.ulfric.dragoon.ObjectFactory;

class AsynchronousTest {

	@Test
	void testRunsAsynchronously() throws Exception {
		ObjectFactory factory = new ObjectFactory();
		GetThreadAsynchronous get = factory.request(GetThreadAsynchronous.class);
		Truth.assertThat(get.get().get()).isNotEqualTo(Thread.currentThread());
	}

	public static class GetThreadAsynchronous {
		@Asynchronous
		public Future<Thread> get() {
			return AsynchronousResult.of(Thread.currentThread());
		}
	}

}
