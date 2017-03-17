package com.ulfric.dragoon.constrain;

public class NoOpAdapter implements ConstraintAdapter<Object> {

	@Override
	public void check(Object object) throws ConstraintException
	{

	}

	@Override
	public Class<Object> adaptionType()
	{
		return Object.class;
	}

}
