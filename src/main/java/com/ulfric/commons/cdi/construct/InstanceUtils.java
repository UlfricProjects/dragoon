package com.ulfric.commons.cdi.construct;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.exception.ExceptionUtils;

public enum InstanceUtils {

	;

	public static <T> T getInstance(Class<T> clazz)
	{
		if (clazz.isEnum())
		{
			T[] constants = clazz.getEnumConstants();

			if (constants.length == 0)
			{
				// TODO throw exception
			}

			return constants[0];
		}

		try
		{
			Constructor<T> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (NoSuchMethodException | SecurityException | InstantiationException |
				IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			return ExceptionUtils.rethrow(e);
		}
	}

}