package com.ulfric.dragoon.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Methods {

	// TODO this method is way too big
	public static List<Method> getOverridableMethods(Class<?> type)
	{
		Map<String, List<Method>> result = new LinkedHashMap<>();

		Class<?> currentType = type;
		do
		{
			declaredMethods: for (Method method : type.getDeclaredMethods())
			{
				if (isOverridable(method))
				{
					String name = method.getName();
					List<Method> methods = result.computeIfAbsent(name, ignore -> new ArrayList<>());

					if (!methods.isEmpty())
					{
						Class<?>[] parameterTypes = method.getParameterTypes();

						for (Method existingMethod : methods)
						{
							if (existingMethod.getName().equals(name))
							{
								Class<?>[] existingParameterTypes = method.getParameterTypes(); // TODO caching

								if (Arrays.equals(parameterTypes, existingParameterTypes))
								{
									continue declaredMethods;
								}
							}
						}
					}

					methods.add(method);
				}
			}

			currentType = currentType.getSuperclass();
		}
		while (!isRootType(currentType));

		return result.values()
				.stream()
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private static boolean isOverridable(Method method)
	{
		if (method.isSynthetic())
		{
			return false;
		}

		int modifiers = method.getModifiers();
		return !Modifier.isStatic(modifiers)
				&& !Modifier.isPrivate(modifiers)
				&& !Modifier.isFinal(modifiers)
				&& !Modifier.isNative(modifiers);
	}

	private static boolean isRootType(Class<?> type)
	{
		return type == Object.class || type == null;
	}

	private Methods() { }

}