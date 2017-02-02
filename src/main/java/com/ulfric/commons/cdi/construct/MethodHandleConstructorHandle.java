package com.ulfric.commons.cdi.construct;

import com.ulfric.commons.exception.Try;

import java.lang.invoke.MethodHandle;

final class MethodHandleConstructorHandle implements ConstructorHandle {
	
	private final MethodHandle handle;
	
	MethodHandleConstructorHandle(MethodHandle handle)
	{
		this.handle = handle;
	}
	
	@Override
	public Object invoke()
	{
		return Try.to(this::invokeWithoutAmbiguousType);
	}
	
	@Override
	public Object invoke(Object... args)
	{
		return Try.to(() -> handle.invokeWithArguments(args));
	}
	
	private Object invokeWithoutAmbiguousType() throws Throwable
	{
		return this.handle.invoke();
	}
	
}