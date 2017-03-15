package com.ulfric.dragoon;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.scope.Scoped;
import com.ulfric.commons.exception.Try;
import com.ulfric.commons.reflect.HandleUtils;

final class Initializer {

	private static final Map<Class<?>, List<Initializable>> INITIALIZE_METHODS = new IdentityHashMap<>();

	void initializeScoped(Scoped<?> scoped)
	{
		Object toInitialize = scoped.read();
		this.initializeObject(toInitialize);
	}

	void initializeObject(Object object)
	{
		Class<?> type = object.getClass();
		Initializer.getInitializables(type).forEach(initializable -> initializable.initialize(object));
	}

	private static List<Initializable> getInitializables(Class<?> clazz)
	{
		return Initializer.INITIALIZE_METHODS.computeIfAbsent(clazz, Initializer::createInitializables);
	}

	private static List<Initializable> createInitializables(Class<?> clazz)
	{
		return Stream.concat(Stream.of(clazz.getDeclaredMethods()), Stream.of(clazz.getMethods()))
				.distinct()
				.filter(Initializer::isInitializable)
				.map(Initializable::new)
				.collect(Collectors.toList());
	}

	private static boolean isInitializable(Method method)
	{
		return method.isAnnotationPresent(Initialize.class);
	}

	private static final class Initializable
	{
		private final MethodHandle methodHandle;

		Initializable(Method method)
		{
			method.setAccessible(true);
			this.methodHandle = HandleUtils.getGenericMethod(method);
		}

		void initialize(Object holder)
		{
			Try.to(() -> this.invoke(holder));
		}

		private void invoke(Object holder) throws Throwable
		{
			this.methodHandle.invokeExact(holder);
		}
	}

}
