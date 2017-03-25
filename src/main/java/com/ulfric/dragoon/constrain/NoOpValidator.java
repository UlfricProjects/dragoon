package com.ulfric.dragoon.constrain;

import java.lang.reflect.Field;

public class NoOpValidator implements ConstraintValidator<Object> {

	@Override
	public void check(Field field, Object object)
	{

	}

	@Override
	public Class<Object> validationType()
	{
		return Object.class;
	}

	@Override
	public String errorMessage()
	{
		return "";
	}

}
