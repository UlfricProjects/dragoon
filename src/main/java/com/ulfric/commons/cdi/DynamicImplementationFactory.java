package com.ulfric.commons.cdi;

import java.lang.reflect.Modifier;

final class DynamicImplementationFactory implements ImplementationFactory {

	@Override
	public <T> Class<? extends T> createImplementationClass(Class<T> parent)
	{
		if (!this.isImplemented(parent))
		{
			return null;
		}

		if (!this.isExtendable(parent))
		{
			return parent;
		}

		return new DynamicSubclassBuilder<>(parent).build();
	}

	private boolean isImplemented(Class<?> clazz)
	{
		return !clazz.isInterface() && !this.isAbstract(clazz);
	}

	private boolean isAbstract(Class<?> clazz)
	{
		return Modifier.isAbstract(clazz.getModifiers());
	}

	private boolean isExtendable(Class<?> clazz)
	{
		return !this.isFinal(clazz) && !clazz.isPrimitive() && !clazz.isArray();
	}

	private boolean isFinal(Class<?> clazz)
	{
		return Modifier.isFinal(clazz.getModifiers());
	}

}