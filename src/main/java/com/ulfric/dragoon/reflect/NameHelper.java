package com.ulfric.dragoon.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class NameHelper {

	public static String getName(Object object) {
		if (object == null) {
			return null;
		}

		if (object.getClass().isArray()) {
			int length = Array.getLength(object);
			for (int index = 0; index < length; index++) {
				String name = getName(Array.get(object, index));
				if (name != null) {
					return name;
				}
			}

			return null;
		}

		Method getName = Methods.getPublicMethod(object.getClass(), "getName");
		if (getName != null && !isVoid(getName)) {
			try {
				Object name = null;
				if (Modifier.isStatic(getName.getModifiers())) {
					name = getName.invoke(null);
				} else {
					name = getName.invoke(object);
				}

				if (name != null) {
					return name.toString();
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException thatsOk) {
			}
		}

		return object.toString();
	}

	private static boolean isVoid(Method method) {
		Class<?> returnType = method.getReturnType();
		return returnType != void.class && returnType != Void.class;
	}

	private NameHelper() {
	}

}