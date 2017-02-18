package com.ulfric.commons.cdi.interceptors;

import java.lang.reflect.Executable;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class InitializeInterceptorTest {

	private Interceptor interceptor;
	private Context context;

	@BeforeEach
	void init()
	{
		this.interceptor = new InitializeInterceptor();
		this.context(this::call);
	}

	@Test
	void testReturnsResult()
	{
		Verify.that(this.interceptor.intercept(this.context)).isEqualTo("hello");
	}

	@Test
	void testAllowsOnlyOneInvocation()
	{
		this.interceptor.intercept(this.context);
		Verify.that(() -> this.interceptor.intercept(this.context)).doesThrow(IllegalStateException.class);
	}

	private String call()
	{
		return "hello";
	}

	private void context(Callable<?> callable)
	{
		this.context = Context.createInvocation(this, IterableUtils.emptyIterable(),
				callable, Mockito.mock(Executable.class), new Object[0]);
	}

}