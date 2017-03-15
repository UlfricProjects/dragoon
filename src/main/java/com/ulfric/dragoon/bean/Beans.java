package com.ulfric.dragoon.bean;

import com.ulfric.commons.bean.Bean;
import com.ulfric.dragoon.construct.InstanceUtils;

public enum Beans {

	;

	public static <T> T create(Class<T> clazz)
	{
		if (clazz.isAssignableFrom(Bean.class))
		{
			return InstanceUtils.createOrNull(clazz);
		}

		if (clazz.isInterface())
		{
			BeanBuilder<T> builder = new BeanBuilder<>(clazz);

			return builder.build();
		}

		throw new BeanCreationException("Class does not extend Bean or is not interface");
	}

	public static class BeanCreationException extends RuntimeException
	{
		private BeanCreationException(String message)
		{
			super(message);
		}
	}

}
