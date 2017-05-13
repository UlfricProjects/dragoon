package com.ulfric.dragoon;

import java.util.Arrays;

public class RequestFailedException extends RuntimeException {

	private static String arguments(Object[] arguments)
	{
		return arguments.length == 0 ? "" : " Arguments: " + Arrays.toString(arguments);
	}

	public RequestFailedException(Class<?> type, Object[] arguments)
	{
		super("Request failed: " + type + RequestFailedException.arguments(arguments));
	}

}