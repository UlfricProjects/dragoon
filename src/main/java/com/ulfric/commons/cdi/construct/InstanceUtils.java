package com.ulfric.commons.cdi.construct;

import com.ulfric.commons.collect.MapUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;

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
		ConstructorHandle constructor = InstanceUtils.getOrCreateConstructor(clazz);
		@SuppressWarnings("unchecked") 
		T instance = (T) constructor.invoke();
		return instance;
	}
	
	public static <T> T createOrNullArgs(Class<T> clazz, Object... args) 
	{
		Objects.requireNonNull(clazz);
		
		if (clazz.isEnum())
		{
			return InstanceUtils.getFirstEnumValueOrNull(clazz);
		}
		else
		{
			@SuppressWarnings("unchecked")
			T instance = (T) InstanceUtils.getOrCreateConstructor(clazz).invoke(args);
			return instance;
		}
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
			Constructor<?> constructor;
			Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
			if (declaredConstructors.length != 0) 
			{
				constructor = declaredConstructors[0];
			} 
			else 
			{
				constructor = clazz.getDeclaredConstructor();
			}
			constructor.setAccessible(true);
			MethodHandle handle = MethodHandles.lookup().unreflectConstructor(constructor);
			// TODO: 2/3/2017 Forget what I'm meant to do here???? 
//			handle = handle.asType(MethodType.methodType(Object.class));
			return new MethodHandleConstructorHandle(handle);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException ignore)
		{
			return EmptyConstructorHandle.INSTANCE;
		}
	}

}