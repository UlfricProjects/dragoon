package com.ulfric.dragoon;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ulfric.commons.exception.Try;
import com.ulfric.dragoon.intercept.Context;
import com.ulfric.dragoon.intercept.Interceptor;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

public final class BytebuddyInterceptor {

	private static final Map<Method, Method> GENERATED_METHODS = new IdentityHashMap<>();

	static BytebuddyInterceptor newInstance(List<Interceptor> pipeline)
	{
		List<Interceptor> defensivePipeline = Collections.unmodifiableList(new ArrayList<>(pipeline));
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
	                        @SuperCall Callable<?> finalDestination,
	                        @Origin Method destinationExecutable)
	{
		Method destination = BytebuddyInterceptor.GENERATED_METHODS.computeIfAbsent(destinationExecutable,
				unwrap -> this.unwrapDestination(owner, unwrap));

		return Context.createInvocation(owner, this.pipeline, finalDestination, destination, arguments)
				.proceed();
	}

	private Method unwrapDestination(Object owner, Method unwrap)
	{
		return Try.to(() -> owner.getClass().getDeclaredMethod(unwrap.getName(), unwrap.getParameterTypes()));
	}

}