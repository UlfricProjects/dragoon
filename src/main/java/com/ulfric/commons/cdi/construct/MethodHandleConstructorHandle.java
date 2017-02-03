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
	public Object invoke(Object... args)
	{
		return Try.to(() -> this.handle.invokeWithArguments(args));
	}
}