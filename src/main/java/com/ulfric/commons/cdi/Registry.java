package com.ulfric.commons.cdi;

import java.util.IdentityHashMap;
import java.util.Map;

abstract class Registry<T extends Registry<T, R>, R> extends Child<T> {

	final Map<Class<?>, R> registered = new IdentityHashMap<>();

	Registry()
	{
		
	}

	Registry(T parent)
	{
		super(parent);
	}

	final Binding createBinding(Class<?> request)
	{
		return new Binding(this, request);
	}

	final R getRegisteredBinding(Class<?> request)
	{
		R implementation = this.registered.get(request);

		if (implementation == null)
		{
			implementation = this.getRegisteredBindingFromParent(request);
		}

		return implementation;
	}

	private R getRegisteredBindingFromParent(Class<?> request)
	{
		return this.hasParent() ? this.getParent().getRegisteredBinding(request) : null;
	}

	abstract void registerBinding(Class<?> request, Class<?> implementation);

}