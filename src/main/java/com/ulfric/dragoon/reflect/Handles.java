package com.ulfric.dragoon.reflect;

import com.ulfric.dragoon.exception.Try;
import com.ulfric.dragoon.exception.Try.CheckedSupplier;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

public class Handles {

	public static MethodHandle setter(Field field)
	{
		return Handles.accessibly(field, () -> Handles.generic(MethodHandles.lookup().unreflectSetter(field)));
	}

	private static <R> R accessibly(AccessibleObject accessible, CheckedSupplier<R> run)
	{
		boolean defaultAccessible = accessible.isAccessible();
		try
		{
			accessible.setAccessible(true);
			return Try.to(run);
		}
		finally
		{
			accessible.setAccessible(defaultAccessible);
		}
	}

	public static MethodHandle generic(MethodHandle handle)
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