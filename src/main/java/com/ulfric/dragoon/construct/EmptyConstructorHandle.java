package com.ulfric.dragoon.construct;

enum EmptyConstructorHandle implements ConstructorHandle {

	INSTANCE;

	@Override
	public Object invoke()
	{
		return null;
	}
}