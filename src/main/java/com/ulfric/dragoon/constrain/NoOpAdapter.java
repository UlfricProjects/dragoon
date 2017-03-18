package com.ulfric.dragoon.constrain;

import java.lang.reflect.Field;

public class NoOpAdapter implements ConstraintAdapter<Object> {

	@Override
	public void check(Field field, Object object) throws ConstraintException
	{

	}

	@Override
	public Class<Object> adaptionType()
	{
		return Object.class;
	}

	@Override
	public String errorMessage()
	{
		return "";
	}

}
