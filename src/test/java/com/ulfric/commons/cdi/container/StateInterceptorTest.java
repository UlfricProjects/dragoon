package com.ulfric.commons.cdi.container;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.commons.naming.Name;
import com.ulfric.commons.naming.Named;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class StateInterceptorTest {

	private final StateInterceptorImpl stateInterceptor = new StateInterceptorImpl();

	@Mock
	private Object owner;
	@Mock
	private Iterable<Interceptor> pipeline;
	@Mock
	private Callable<?> finalDestination;
	@Mock
	private Iterator<Interceptor> pipelineIterator;

	private Object[] arguments = new Object[0];

	private Context invocation;

	@BeforeEach
	void init()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(this.pipeline.iterator()).thenReturn(this.pipelineIterator);
		this.invocation = Context.createInvocation(this.owner, this.pipeline, this.finalDestination, this.arguments);
	}

	@Test
	void testGetName_named()
	{

		Verify.that(() ->
				MethodUtils.invokeMethod(
						this.stateInterceptor,
						true,
						"getName",
						this.stateInterceptor
				)
		).valueIsEqualTo(this.stateInterceptor.getName());
	}

	@Test
	void testGetName_valueOf()
	{
		int five = 5;
		String string = String.valueOf(five);

		Verify.that(() ->
				MethodUtils.invokeMethod(
						this.stateInterceptor,
						true,
						"getName",
						five
				)
		).valueIsEqualTo(string);
	}

	@Test
	void testIntercept_noExceptions()
	{
		Verify.that(() ->
		{
			this.stateInterceptor.intercept(this.invocation);
		}).runsWithoutExceptions();
	}

	@Test
	void testIntercept_nonNull()
	{
		Verify.that(() ->
		{
			this.stateInterceptor.intercept(this.invocation);
		}).isNotNull();
	}

	@Name(value = "foo")
	private final class StateInterceptorImpl extends StateInterceptor implements Named
	{

		@Override
		protected void before(String name)
		{

		}

		@Override
		protected void after(String name, long timeToProcessInMillis)
		{

		}

	}

}
