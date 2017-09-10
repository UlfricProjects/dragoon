package com.ulfric.dragoon.naming;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.ulfric.dragoon.reflect.Methods;
import com.ulfric.dragoon.stereotype.Stereotypes;

public class NameHelper {

	public static String getName(Object object) { // TODO cleanup method
		if (object == null) {
			return null;
		}

		if (object instanceof String) {
			return (String) object;
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

		if (object instanceof AnnotatedElement) {
			Name nameAnnotation = Stereotypes.getFirst((AnnotatedElement) object, Name.class);
			if (nameAnnotation != null) {
				return nameAnnotation.value();
			}
		}

		Name nameAnnotation = Stereotypes.getFirst(object.getClass(), Name.class);
		if (nameAnnotation != null) {
			return nameAnnotation.value();
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

	private NameHelper() {}

}
