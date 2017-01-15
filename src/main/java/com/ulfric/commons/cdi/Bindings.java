package com.ulfric.commons.cdi;

import java.util.Map;

import com.ulfric.commons.collect.MapUtils;

final class Bindings extends Child<Bindings> {

	private final Map<Class<?>, Class<?>> bindings = MapUtils.newSynchronizedIdentityHashMap();

	Bindings()
	{
		
	}

	Bindings(Bindings parent)
	{
		super(parent);
	}

	<T> Binding<T> createBinding(Class<T> request)
	{
		return new Binding<>(this, request);
	}

	Class<?> getOrTryToCreateBinding(Class<?> request, ImplementationFactory implementationFactory)
	{
		Class<?> implementation = this.getRegisteredBinding(request);

		if (implementation == null)
		{
			implementation = implementationFactory.createImplementationClass(request);

			if (implementation != null)
			{
				this.registerBinding(request, implementation);
			}
		}

		return implementation;
	}

	private Class<?> getRegisteredBinding(Class<?> request)
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

	void registerBinding(Class<?> request, Class<?> implementation)
	{
		this.bindings.put(request, implementation);
	}

}