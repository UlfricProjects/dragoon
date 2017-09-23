package com.ulfric.dragoon.extension.intercept.asynchronous;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.ObjectFactory;

import java.util.concurrent.Future;

class AsynchronousTest {

	@Test
	void testRunsAsynchronously() throws Exception {
		ObjectFactory factory = new ObjectFactory();
		GetThreadAsynchronous get = factory.request(GetThreadAsynchronous.class);
		Future<Thread> future = get.get();
		while (!future.isDone()) {
			Thread.sleep(1L);
		}
		Truth.assertThat(future.get()).isNotEqualTo(Thread.currentThread());
	}

	public static class GetThreadAsynchronous {
		@Asynchronous
		public Future<Thread> get() {
			return AsynchronousResult.of(Thread.currentThread());
		}
	}

}
