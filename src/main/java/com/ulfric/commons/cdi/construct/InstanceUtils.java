package com.ulfric.commons.cdi.construct;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;

import com.ulfric.commons.collect.MapUtils;

public enum InstanceUtils {

	;

	private static final Map<Class<?>, ConstructorHandle> CONSTRUCTORS = MapUtils.newSynchronizedIdentityHashMap();

	public static <T> T createOrNull(Class<T> clazz)
	{
		Objects.requireNonNull(clazz);

		if (clazz.isEnum())
		{
			return InstanceUtils.getFirstEnumValueOrNull(clazz);
		}

		@SuppressWarnings("unchecked")
		T instance = (T) InstanceUtils.getOrCreateConstructor(clazz).invoke();
		return instance;
	}

	private static <E> E getFirstEnumValueOrNull(Class<E> clazz)
	{
		E[] constants = clazz.getEnumConstants();

		if (constants.length == 0)
		{
			return null;
		}

		return constants[0];
	}

	private static ConstructorHandle getOrCreateConstructor(Class<?> clazz)
	{
		return InstanceUtils.CONSTRUCTORS.computeIfAbsent(clazz, InstanceUtils::createConstructor);
	}

	private static ConstructorHandle createConstructor(Class<?> clazz)
	{
		try
		{
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			MethodHandle handle = MethodHandles.lookup().unreflectConstructor(constructor);
			handle = handle.asType(MethodType.methodType(Object.class));
			return new MethodHandleConstructorHandle(handle);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException ignore)
		{
			return EmptyConstructorHandle.INSTANCE;
		}
	}

}