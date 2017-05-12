package com.ulfric.dragoon.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.function.Supplier;

public class Handles {

	public static MethodHandle constructor(Constructor<?> constructor)
	{
		return Handles.accessibly(constructor, () ->
		{
			try
			{
				return Handles.generic(MethodHandles.lookup().unreflectConstructor(constructor));
			}
			catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		});
	}

	public static MethodHandle getter(Field field)
	{
		return Handles.accessibly(field, () ->
		{
			try
			{
				return Handles.generic(MethodHandles.lookup().unreflectGetter(field));
			}
			catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		});
	}

	public static MethodHandle setter(Field field)
	{
		return Handles.accessibly(field, () ->
		{
			try
			{
				return Handles.generic(MethodHandles.lookup().unreflectSetter(field));
			}
			catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		});
	}

	private static <R> R accessibly(AccessibleObject accessible, Supplier<R> run)
	{
		boolean defaultAccessible = accessible.isAccessible();
		try
		{
			accessible.setAccessible(true);
			return run.get();
		}
		finally
		{
			accessible.setAccessible(defaultAccessible);
		}
	}

	private static MethodHandle generic(MethodHandle handle)
	{
		MethodType original = handle.type();
		MethodType generic = original.generic();

		if (original.returnType() == void.class)
		{
			generic = generic.changeReturnType(void.class);
		}

		return handle.asType(generic);
	}

	private Handles() { }

}