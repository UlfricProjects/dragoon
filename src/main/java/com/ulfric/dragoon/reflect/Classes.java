package com.ulfric.dragoon.reflect;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;

import com.ulfric.dragoon.Dynamic;

public class Classes {

	public static <T> DynamicType.Builder<T> extend(Class<T> type)
	{
		return new ByteBuddy().subclass(type, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
				.annotateType(type.getAnnotations())
				.implement(Dynamic.class);
	}

	public static boolean isRoot(Class<?> type)
	{
		return type == Object.class || type == null;
	}

	public static boolean isDescendedFromClassLoader(Class<?> type, Class<? extends ClassLoader> parent)
	{
		return Classes.isClassLoaderDescendedFromClassLoader(type.getClassLoader(), parent);
	}

	private static boolean isClassLoaderDescendedFromClassLoader(ClassLoader loader, Class<? extends ClassLoader> parent)
	{
		if (loader == null)
		{
			return false;
		}

		if (parent.isInstance(loader))
		{
			return true;
		}

		return Classes.isClassLoaderDescendedFromClassLoader(loader.getParent(), parent);
	}

	private Classes() { }

}