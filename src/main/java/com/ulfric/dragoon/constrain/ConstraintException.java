package com.ulfric.dragoon.constrain;

import java.lang.reflect.Field;

public class ConstraintException extends RuntimeException {

	public ConstraintException(ConstraintValidator<?> adapter, Field field)
	{
		super(
				field.getClass().getName() +
						" {" + field.getName() + " [" + field.getType() + "]} : " +
						adapter.errorMessage()
		);
	}

}
