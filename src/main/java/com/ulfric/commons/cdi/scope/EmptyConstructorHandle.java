package com.ulfric.commons.cdi.scope;

enum EmptyConstructorHandle implements ConstructorHandle {

	INSTANCE;

	@Override
	public Object invoke()
	{
		return null;
	}

}