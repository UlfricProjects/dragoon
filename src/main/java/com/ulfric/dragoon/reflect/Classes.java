package com.ulfric.dragoon.reflect;

import java.lang.reflect.Type;

import com.ulfric.dragoon.Dynamic;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;

public class Classes {

	public static <T> DynamicType.Builder<T> extend(Class<T> type) {
		return new ByteBuddy().subclass(type, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
		        .annotateType(type.getAnnotations()).implement(Dynamic.class);
	}

	public static <T> Class<? extends T> translate(Class<T> type, ClassLoader owner) {
		return Classes.extend(type).make().load(owner).getLoaded();
	}

	public static <T> Class<? extends T> implement(Class<T> type, Type... implement) {
		return extend(type)
				.implement(implement)
				.make()
				.load(type.getClassLoader())
				.getLoaded();
	}

	public static Class<?> getNonDynamic(Class<?> type) {
		if (type == null) {
			return null;
		}

		if (!Dynamic.class.isAssignableFrom(type)) {
			return type;
		}

		return Classes.getNonDynamic(type.getSuperclass());
	}

	public static boolean isRoot(Class<?> type) {
		return type == Object.class || type == null;
	}

	public static Class<?> getClass(Object object) {
		if (object == null) {
			return Object.class;
		}

		return object.getClass();
	}

	private Classes() {}

}
