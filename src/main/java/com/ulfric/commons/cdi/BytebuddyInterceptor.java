package com.ulfric.commons.cdi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.commons.cdi.intercept.Invocation;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

final class BytebuddyInterceptor {

	public static BytebuddyInterceptor newInstance(List<Interceptor> pipeline)
	{
		List<Interceptor> defensivePipeline = new ArrayList<>(pipeline);
		return new BytebuddyInterceptor(defensivePipeline);
	}

	private final List<Interceptor> pipeline;

	private BytebuddyInterceptor(List<Interceptor> pipeline)
	{
		this.pipeline = pipeline;
	}

	@RuntimeType
	public Object intercept(@This Object owner,
							@AllArguments Object[] arguments,
							@SuperCall Callable<?> finalDestination)
	{
		return Invocation.createInvocation(owner, this.pipeline, finalDestination, arguments).proceed();
	}

}