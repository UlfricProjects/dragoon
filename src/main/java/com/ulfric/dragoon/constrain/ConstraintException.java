package com.ulfric.dragoon.constrain;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ulfric.commons.exception.Try;
import com.ulfric.commons.reflect.HandleUtils;

public class ConstraintException extends RuntimeException {

	private static final Map<Annotation, MethodHandle> ERROR_METHODS = new HashMap<>();

	public ConstraintException(ConstraintValidator<?> validator)
	{
		super(ConstraintException.getError(validator));
	}

	private static String getError(ConstraintValidator<?> validator)
	{
		Annotation annotation = Constraints.getAnnotation(validator.getClass());

		MethodHandle error = ConstraintException.ERROR_METHODS.computeIfAbsent(annotation, ConstraintException::getErrorHandle);

		if (error == null)
		{
			return "Validation failed";
		}

		return (String) Try.to(() -> error.invokeExact((Object) annotation));
	}

	private static MethodHandle getErrorHandle(Annotation annotation)
	{
		Method method;
		try
		{
			method = annotation.annotationType().getDeclaredMethod("error");
		}
		catch (NoSuchMethodException e)
		{
			return null;
		}
		return HandleUtils.getGenericMethod(method);
	}

}
