package com.ulfric.commons.cdi.intercept.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.construct.BeanFactory;

@RunWith(JUnitPlatform.class)
public class AsyncTest {

	private final BeanFactory factory = BeanFactory.newInstance();
	private final AsyncInterceptTest asyncTest = (AsyncInterceptTest) this.factory.request(AsyncInterceptTest.class);

	/*@Test
	public void test_asyncIntercept_completableFuture()
	{
		Object object = this.asyncTest.returnObject();
		Verify.that(object).isExactType(CompletableFuture.class);
	}

	@Test
	public void test_asyncIntercept_nestedFuture() throws ExecutionException, InterruptedException
	{
		Future<?> future = this.asyncTest.returnFuture();

		Verify.that(future).isExactType(CompletableFuture.class);

		while (!future.isDone())
		{}

		Verify.that(future.get()).isExactType(String.class);
	}

	@Test
	public void test_asyncIntercept_catchException()
	{
		Verify.that(() ->
		{
			this.asyncTest.crash();
			while (AsyncTest.crashThread == null)
			{}
			AsyncTest.crashThread.interrupt();
		}).runsWithoutExceptions();
		this.killTestThread();
	}*/

	private void killTestThread()
	{
		new Thread(() ->
		{
			try
			{
				Thread.sleep(500);
				AsyncTest.crashThread = null;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	private static volatile Thread crashThread = null;

	public static class AsyncInterceptTest
	{

		@Asynchronous
		public Object returnObject()
		{
			return new Object();
		}

		@Asynchronous
		public Future<?> returnFuture()
		{
			return CompletableFuture.completedFuture("foo");
		}

		@Asynchronous
		public Future<?> crash()
		{
			AsyncTest.crashThread = Thread.currentThread();
			return CompletableFuture.supplyAsync(() ->
			{
				while (AsyncTest.crashThread != null)
				{}
				return null;
			});
		}

	}

}
