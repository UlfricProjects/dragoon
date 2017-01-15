package com.ulfric.commons.cdi;

import java.util.Objects;

import com.ulfric.commons.exception.Try;

public class ObjectFactory extends Child<ObjectFactory> {

	public static ObjectFactory newInstance()
	{
		return new ObjectFactory();
	}

	private final Bindings bindings;

	private ObjectFactory()
	{
		this.bindings = new Bindings();
	}

	private ObjectFactory(ObjectFactory parent)
	{
		super(parent);

		this.bindings = new Bindings(parent.bindings);
	}

	ObjectFactory subfactory()
	{
		return new ObjectFactory(this);
	}

	public <T> Binding<T> bind(Class<T> request)
	{
		Objects.requireNonNull(request);

		return this.bindings.createBinding(request);
	}

	public Object request(Class<?> request)
	{
		Objects.requireNonNull(request);

		Class<?> implementation = this.bindings.getRegisteredBinding(request);

		if (implementation == null)
		{
			return null;
		}

		return Try.to(implementation::newInstance);
	}

}