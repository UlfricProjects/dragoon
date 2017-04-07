package com.ulfric.dragoon.constrain;

import java.security.InvalidParameterException;

public class ConstraintTypeMismatchException extends InvalidParameterException {

	ConstraintTypeMismatchException(String message)
	{
		super(message);
	}

}
