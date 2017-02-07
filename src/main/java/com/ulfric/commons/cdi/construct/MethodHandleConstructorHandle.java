package com.ulfric.commons.cdi.construct;

import java.lang.invoke.MethodHandle;

import com.ulfric.commons.exception.Try;

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

	private Object invokeWithoutAmbiguousType() throws Throwable
	{
		return this.handle.invokeExact();
	}

}