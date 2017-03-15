package com.ulfric.dragoon.bean;

import java.lang.reflect.Method;
import java.util.function.Predicate;

final class GetterPredicate implements Predicate<Method> {

	private static final String METHOD_PREFIX = "get";
	private static final int NEXT_CHAR_LOCATION = 3;
	private static final Class<?> NOT_RETURN_TYPE = Void.TYPE;
	private static final int PARAMETER_COUNT = 0;

	@Override
	public boolean test(Method method)
	{
		String name = method.getName().toLowerCase();

		return name.startsWith(GetterPredicate.METHOD_PREFIX) &&
				Character.isUpperCase(method.getName().charAt(GetterPredicate.NEXT_CHAR_LOCATION)) &&
				!method.getReturnType().equals(GetterPredicate.NOT_RETURN_TYPE) &&
				method.getParameterCount() == GetterPredicate.PARAMETER_COUNT;
	}

}