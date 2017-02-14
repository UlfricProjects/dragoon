package com.ulfric.commons.cdi;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Scoped;
import com.ulfric.commons.exception.Try;
import com.ulfric.commons.reflect.HandleUtils;

final class Injector {

	private static final Map<Class<?>, List<Injectable>> FIELDS = new IdentityHashMap<>();
	private final ObjectFactory factory;

	Injector(ObjectFactory factory)
	{
		this.factory = factory;
	}

	void injectFields(Scoped<?> scoped)
	{
		if (scoped.isRead())
		{
			return;
		}

		Object injectInto = scoped.read();
		this.injectValuesIntoObject(injectInto);
	}

	private void injectValuesIntoObject(Object object)
	{
		Class<?> type = object.getClass();
		Injector.getInjectables(type).forEach(injectable -> injectable.inject(object, this.factory));
	}

	private static List<Injectable> getInjectables(Class<?> clazz)
	{
		return Injector.FIELDS.computeIfAbsent(clazz, Injector::createInjectables);
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

	public static class InjectException extends RuntimeException 
	{
		public InjectException(String message)
		{
			super(message);
		}
	}
}