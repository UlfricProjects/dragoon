package com.ulfric.commons.cdi.construct;

enum EmptyConstructorHandle implements ConstructorHandle {

	INSTANCE;

	@Override
	public Object invoke(Object... args)
	{
		return null;
	}
}