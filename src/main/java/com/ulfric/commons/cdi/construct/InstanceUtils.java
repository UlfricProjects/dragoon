package com.ulfric.commons.cdi.construct;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum InstanceUtils {

	;

	public static <T> T getInstance(Class<T> clazz)
	{
		if (clazz.isEnum())
		{
			T[] constants = clazz.getEnumConstants();

			if (constants.length == 0)
			{
				throw new IllegalArgumentException("Enum isn't a singleton");
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
			throw new RuntimeException(e);
		}
	}

}