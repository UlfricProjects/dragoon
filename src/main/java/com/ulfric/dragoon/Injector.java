package com.ulfric.dragoon;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.ulfric.commons.exception.Try;
import com.ulfric.commons.reflect.ClassUtils;
import com.ulfric.commons.reflect.HandleUtils;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.scope.Scoped;

final class Injector {

	static final String SCOPE_READ_TYPE = "inject";
	private static final Map<Class<?>, List<Injectable>> INJECTABLE_FIELDS = new IdentityHashMap<>();
	private static final Map<Class<?>, List<BiConsumer<Object, ObjectFactory>>> INJECTION_LAYERS =
			new IdentityHashMap<>();

	private final ObjectFactory factory;

	Injector(ObjectFactory factory)
	{
		this.factory = factory;
	}

	Iterator<Runnable> getInjections(Scoped<?> scoped)
	{
		if (scoped.isRead(Injector.SCOPE_READ_TYPE))
		{
			return Collections.emptyIterator();
		}

		Object injectInto = scoped.read(Injector.SCOPE_READ_TYPE);
		return this.getInjectionsList(injectInto).iterator();
	}

	private List<Runnable> getInjectionsList(Object injectInto)
	{
		return Injector.INJECTION_LAYERS.computeIfAbsent(injectInto.getClass(), this::createInjectionsList)
				.stream()
				.map(consumer -> (Runnable) () -> consumer.accept(injectInto, this.factory))
				.collect(Collectors.toList());
	}

	private List<BiConsumer<Object, ObjectFactory>> createInjectionsList(Class<?> youngest)
	{
		return ClassUtils.getHeirarchy(youngest)
				.stream()
				.map(Injector::getInjectables)
				.flatMap(List::stream)
				.map(injector ->
					(BiConsumer<Object, ObjectFactory>) injector::inject)
				.collect(Collectors.toList());
	}

	private static List<Injectable> getInjectables(Class<?> injectInto)
	{
		return Injector.INJECTABLE_FIELDS.computeIfAbsent(injectInto, Injector::createInjectables);
	}

	private static List<Injectable> createInjectables(Class<?> injectInto)
	{
		return Arrays.stream(injectInto.getDeclaredFields())
				.filter(Injector::isInjectable)
				.map(Injectable::new)
				.collect(Collectors.toList());
	}

	private static boolean isInjectable(Field field)
	{
		return field.isAnnotationPresent(Inject.class);
	}

	private static final class Injectable
	{
		private final MethodHandle fieldHandle;
		private final Class<?> type;

		Injectable(Field field)
		{
			field.setAccessible(true);
			this.fieldHandle = HandleUtils.createGenericSetter(field);
			this.type = field.getType();
		}

		void inject(Object holder, ObjectFactory factory)
		{
			Try.to(() -> this.invoke(holder, factory.request(this.type)));
		}

		private void invoke(Object holder, Object value) throws Throwable
		{
			this.fieldHandle.invokeExact(holder, value);
		}
	}
}