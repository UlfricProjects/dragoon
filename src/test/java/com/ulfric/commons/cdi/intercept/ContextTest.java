package com.ulfric.commons.cdi.intercept;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ContextTest {

	private final Context context;

	public ContextTest()
	{
		Object owner = new Object();
		Object[] arguments = new Object[0];
		Iterator<Interceptor> interceptors = new ArrayList<Interceptor>().iterator();

		this.context = new Context(owner, null, arguments, null, interceptors);
	}

	@Test
	public void test_getOwner_returnsNonNull()
	{
		Verify.that(this.context.getOwner()).isNotNull();
	}

	@Test
	public void test_getArguments_returnsNotNull()
	{
		Verify.that(this.context.getArguments()).isNotNull();
	}

	@Test
	public void test_proceed_throwsException()
	{
		Verify.that(this.context::proceed).doesThrow(RuntimeException.class);
	}

}
