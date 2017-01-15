package com.ulfric.commons.cdi;

import java.util.IdentityHashMap;
import java.util.Map;

final class Bindings extends Child<Bindings> {

	private final Map<Class<?>, Class<?>> bindings = new IdentityHashMap<>();

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

	Class<?> getRegisteredBinding(Class<?> request)
	{
		Class<?> binding = this.bindings.get(request);

		if (binding == null && this.hasParent())
		{
			return this.getParent().getRegisteredBinding(request);
		}

		return binding;
	}

	void registerBinding(Class<?> request, Class<?> implementation)
	{
		this.bindings.put(request, implementation);
	}

}