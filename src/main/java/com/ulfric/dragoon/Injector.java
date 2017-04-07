package com.ulfric.dragoon;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.scope.Scoped;
import com.ulfric.commons.exception.Try;
import com.ulfric.commons.reflect.HandleUtils;

final class Injector {

	private static final String SCOPE_READ_TYPE = "inject";
	private static final Map<Class<?>, List<Injectable>> INJECTABLE_FIELDS = new IdentityHashMap<>();
	private final ObjectFactory factory;

	Injector(ObjectFactory factory)
	{
		this.factory = factory;
	}

	void injectFields(Scoped<?> scoped)
	{
		if (scoped.isRead(Injector.SCOPE_READ_TYPE))
		{
			return;
		}

		Object injectInto = scoped.read(Injector.SCOPE_READ_TYPE);
		this.injectValuesIntoObject(injectInto);
	}

	private void injectValuesIntoObject(Object object)
	{
		Class<?> type = object.getClass();
		Injector.getInjectables(type).forEach(injectable -> injectable.inject(object, this.factory));
	}

	private static List<Injectable> getInjectables(Class<?> clazz)
	{
		return Injector.INJECTABLE_FIELDS.computeIfAbsent(clazz, Injector::createInjectables);
	}

	private static List<Injectable> createInjectables(Class<?> clazz)
	{
		return FieldUtils.getAllFieldsList(clazz)
				.stream()
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