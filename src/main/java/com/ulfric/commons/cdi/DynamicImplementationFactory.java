package com.ulfric.commons.cdi;

import java.lang.reflect.Modifier;

final class DynamicImplementationFactory implements ImplementationFactory {

	@Override
	public <T> Class<? extends T> createImplementationClass(Class<T> parent)
	{
		if (!this.isInstantiable(parent))
		{
			return null;
		}

		return new DynamicSubclassBuilder<>(parent).build();
	}

	private boolean isInstantiable(Class<?> clazz)
	{
		return !clazz.isInterface() && !this.isAbstract(clazz);
	}

	private boolean isAbstract(Class<?> clazz)
	{
		return Modifier.isAbstract(clazz.getModifiers());
	}

}