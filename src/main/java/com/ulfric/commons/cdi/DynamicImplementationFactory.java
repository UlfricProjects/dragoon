package com.ulfric.commons.cdi;

import java.lang.reflect.Modifier;

final class DynamicImplementationFactory implements ImplementationFactory {

	@Override
	public Class<?> createImplementationClass(Class<?> parent)
	{
		if (!this.isInstantiable(parent))
		{
			return null;
		}

		return parent;
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