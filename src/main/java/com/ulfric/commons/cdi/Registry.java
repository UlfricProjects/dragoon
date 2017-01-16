package com.ulfric.commons.cdi;

import java.util.IdentityHashMap;
import java.util.Map;

abstract class Registry<T extends Registry<T>> extends Child<T> {

	private final Map<Class<?>, Class<?>> bindings = new IdentityHashMap<>();

	public Registry()
	{
		
	}

	public Registry(T parent)
	{
		super(parent);
	}

	final Binding createBinding(Class<?> request)
	{
		return new Binding(this, request);
	}

	final Class<?> getRegisteredBinding(Class<?> request)
	{
		Class<?> implementation = this.bindings.get(request);

		if (implementation == null)
		{
			implementation = this.getRegisteredBindingFromParent(request);
		}

		return implementation;
	}

	private Class<?> getRegisteredBindingFromParent(Class<?> request)
	{
		return this.hasParent() ? this.getParent().getRegisteredBinding(request) : null;
	}

	final void registerBinding(Class<?> request, Class<?> implementation)
	{
		this.bindings.put(request, implementation);
	}

}