package com.ulfric.dragoon.bean;

import java.util.IdentityHashMap;
import java.util.Map;

import com.ulfric.commons.bean.Bean;
import com.ulfric.dragoon.construct.InstanceUtils;

public enum Beans {

	;

	private static final Map<Class<?>, Class<?>> BINDINGS = new IdentityHashMap<>();

	public static <T> T create(Class<T> clazz)
	{
		if (Bean.class.isAssignableFrom(clazz))
		{
			return InstanceUtils.createOrNull(clazz);
		}

		if (clazz.isInterface())
		{
			@SuppressWarnings("unchecked")
			Class<? extends T> implementation = (Class<? extends T>) Beans.BINDINGS.computeIfAbsent(clazz, Beans::bindToImplementation);

			return InstanceUtils.createOrNull(implementation);
		}

		throw new BeanCreationException("Class does not extend Bean or is not interface");
	}

	private static <T> Class<? extends T> bindToImplementation(Class<T> binding)
	{
		BeanBuilder<T> builder = new BeanBuilder<>(binding);

		return builder.build();
	}

	public static class BeanCreationException extends RuntimeException
	{
		private BeanCreationException(String message)
		{
			super(message);
		}
	}

}
