package com.ulfric.dragoon;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ulfric.commons.exception.Try;
import com.ulfric.commons.reflect.ClassUtils;
import com.ulfric.commons.reflect.HandleUtils;
import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.scope.Scoped;

final class Initializer {

	private static final String SCOPE_READ_TYPE = "init";
	private static final Map<Class<?>, List<Initializable>> INITIALIZE_LAYERS = new IdentityHashMap<>();
	private static final Map<Class<?>, List<Consumer<Object>>> INITIALIZERS = new IdentityHashMap<>();

	Iterator<Runnable> getInitializers(Scoped<?> scoped)
	{
		if (scoped.isRead(Initializer.SCOPE_READ_TYPE))
		{
			return Collections.emptyIterator();
		}

		Object toInitialize = scoped.read(Initializer.SCOPE_READ_TYPE);
		return this.getInitializersList(toInitialize).iterator();
	}

	private List<Runnable> getInitializersList(Object initialize)
	{
		return Initializer.INITIALIZERS.computeIfAbsent(initialize.getClass(),
				youngest ->
					ClassUtils.getHeirarchy(initialize.getClass())
					.stream()
					.map(Initializer::getInitializables)
					.flatMap(List::stream)
					.map(initializer -> (Consumer<Object>) initializer::initialize)
					.collect(Collectors.toList()))
				.stream()
				.map(consumer -> (Runnable) () -> consumer.accept(initialize))
				.collect(Collectors.toList());
	}

	private static List<Initializable> getInitializables(Class<?> clazz)
	{
		return Initializer.INITIALIZE_LAYERS.computeIfAbsent(clazz, Initializer::createInitializables);
	}

	private static List<Initializable> createInitializables(Class<?> clazz)
	{
		return Stream.of(clazz.getDeclaredMethods())
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
