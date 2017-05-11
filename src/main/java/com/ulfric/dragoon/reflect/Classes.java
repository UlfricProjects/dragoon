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

	private Classes() { }

}