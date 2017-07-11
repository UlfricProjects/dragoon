package com.ulfric.dragoon.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Instances {

	public static <T> T instance(Class<T> type, Object... parameters) {
		if (type.isEnum()) {
			for (T enumValue : type.getEnumConstants()) {
				return enumValue;
			}

			return null;
		}

		Constructor<T> constructor = Instances.findConstructor(type, parameters);
		if (constructor == null) {
			return null;
		}

		constructor.setAccessible(true);
		return Instances.newInstance(constructor, parameters);
	}

	private static <T> Constructor<T> findConstructor(Class<T> type, Object... parameters) {
		int length = parameters.length;
		constructors: for (Constructor<?> constructor : type.getDeclaredConstructors()) {
			if (length != constructor.getParameterCount()) {
				continue;
			}

			Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
			for (int x = 0; x < length; x++) {
				if (!constructorParameterTypes[x].isInstance(parameters[x])) {
					continue constructors;
				}
			}

			@SuppressWarnings("unchecked")
			Constructor<T> casted = (Constructor<T>) constructor;
			return casted;
		}

		return null;
	}

	private static <T> T newInstance(Constructor<T> constructor, Object... parameters) {
		try {
			return constructor.newInstance(parameters);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
		        | InvocationTargetException ignore) {
			return null;
		}
	}

	private Instances() {}

}
