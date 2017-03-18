package com.ulfric.dragoon.constrain;

import java.lang.reflect.Field;

public class ConstraintException extends Exception {

	public ConstraintException(ConstraintAdapter<?> adapter, Field field)
	{
		super(
				field.getClass().getName() +
						" {" + field.getName() + " [" + field.getType() + "]} : " +
						adapter.errorMessage()
		);
	}

}
