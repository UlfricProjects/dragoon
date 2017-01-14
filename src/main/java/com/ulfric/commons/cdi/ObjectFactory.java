package com.ulfric.commons.cdi;

import java.util.Objects;

public class ObjectFactory extends Child<ObjectFactory> {

	public static ObjectFactory newInstance()
	{
		return new ObjectFactory();
	}

	private ObjectFactory()
	{
		
	}

	private ObjectFactory(ObjectFactory parent)
	{
		super(parent);
	}

	ObjectFactory subfactory()
	{
		return new ObjectFactory(this);
	}

	public <T> Binding<T> bind(Class<T> request)
	{
		Objects.requireNonNull(request);

		return new Binding<>(request);
	}

	public void request(Class<?> request)
	{
		Objects.requireNonNull(request);
	}

}