package com.ulfric.dragoon.bean;

import java.lang.reflect.Method;
import java.util.function.Function;

final class GetterMapper implements Function<Method, Getter> {

	private static final int NEXT_CHAR_LOCATION = 3;

	@Override
	public Getter apply(Method method)
	{
		String name = this.lowerCaseFirstChar(
				method.getName().substring(GetterMapper.NEXT_CHAR_LOCATION)
		);

		return new Getter(name, method.getReturnType());
	}

	private String lowerCaseFirstChar(String string)
	{
		String firstLetter = string.substring(0, 1);

		return firstLetter.toLowerCase() + string.substring(1);
	}

}