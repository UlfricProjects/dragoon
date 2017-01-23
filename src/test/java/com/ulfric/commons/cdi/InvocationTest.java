package com.ulfric.commons.cdi;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class InvocationTest {

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
	void testGetOwner_returnsOwner()
	{
		Verify.that(this.invocation.getOwner()).isSameAs(this.owner);
	}

	@Test
	void testGetArguments_returnsArguments()
	{
		Verify.that(this.invocation.getArguments()).isSameAs(this.arguments);
	}

}