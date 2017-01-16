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

		if (!this.isExtendable(parent))
		{
			return parent;
		}

		return new DynamicSubclassBuilder<>(parent).build();
	}

	private boolean isInstantiable(Class<?> clazz)
	{
		return !clazz.isInterface() && !this.isAbstract(clazz) && !clazz.isPrimitive() && !clazz.isArray();
	}

	private boolean isAbstract(Class<?> clazz)
	{
		return Modifier.isAbstract(clazz.getModifiers());
	}

	private boolean isExtendable(Class<?> clazz)
	{
		return !this.isFinal(clazz);
	}

	private boolean isFinal(Class<?> clazz)
	{
		return Modifier.isFinal(clazz.getModifiers());
	}

}