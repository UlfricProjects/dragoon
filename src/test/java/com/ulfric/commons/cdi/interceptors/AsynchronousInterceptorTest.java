package com.ulfric.commons.cdi.interceptors;

import java.lang.reflect.Executable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.commons.exception.Try;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class AsynchronousInterceptorTest {

	private Interceptor interceptor;
	private Context context;

	@BeforeEach
	void init()
	{
		this.interceptor = new AsynchronousInterceptor();
		this.context(() -> CompletableFuture.completedFuture("hello"));
	}

	@Test
	void testReturnsResult()
	{
		Future<String> future = this.call();
		String result = Try.to(() -> { return future.get(); });
		Verify.that(result).isEqualTo("hello");
	}

	@Test
	void testExecutesAsync()
	{
		this.context(Thread::currentThread);
		Future<Thread> future = this.call();
		Thread ranOn = Try.to(() -> { return future.get(); });
		Verify.that(ranOn).isNotSameAs(Thread.currentThread());
	}

	@Test
	void testThrowsExecutionExceptionThrowsRuntimeException()
	{
		this.context(() -> { throw new ExecutionException(null); });
		Verify.that(this.call()::get).doesThrow(RuntimeException.class);
	}

	@Test
	void testThrowsInterruptedExceptionThrowsRuntimeException()
	{
		this.context(() -> { throw new InterruptedException(); });
		Verify.that(this.call()::get).doesThrow(RuntimeException.class);
	}

	@SuppressWarnings("unchecked")
	private <T> Future<T> call()
	{
		return (Future<T>) this.interceptor.intercept(this.context);
	}

	private void context(Callable<?> callable)
	{
		this.context = Context.createInvocation(this, IterableUtils.emptyIterable(),
				callable, Mockito.mock(Executable.class), new Object[0]);
	}

}