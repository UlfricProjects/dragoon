package com.ulfric.dragoon.container;

import com.ulfric.commons.naming.Named;
import com.ulfric.dragoon.Dynamic;

public interface Feature extends Named {

	default boolean isDisabled()
	{
		return !this.isEnabled();
	}

	boolean isEnabled();

	void enable();

	void disable();

	@Override
	default String getName()
	{
		return Named.tryToGetNameFromAnnotation(this).orElseGet(() ->
		{
			Class<?> named = Feature.this.getClass();
			while (Dynamic.class.isAssignableFrom(named))
			{
				named = named.getSuperclass();
			}
			return named.getSimpleName();
		});
	}

}