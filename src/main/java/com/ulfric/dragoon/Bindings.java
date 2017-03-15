package com.ulfric.dragoon;

final class Bindings extends Registry<Bindings, Class<?>> {

	Bindings()
	{

	}

	Bindings(Bindings parent)
	{
		super(parent);
	}

	@Override
	void registerBinding(Class<?> request, Class<?> implementation)
	{
		this.registered.put(request, implementation);
	}

}