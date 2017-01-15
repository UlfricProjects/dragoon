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
		return this.bindings.get(request);
	}

	void registerBinding(Class<?> request, Class<?> implementation)
	{
		this.bindings.put(request, implementation);
	}

}