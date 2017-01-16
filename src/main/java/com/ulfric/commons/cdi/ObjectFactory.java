package com.ulfric.commons.cdi;

import java.util.Objects;

import com.ulfric.commons.exception.Try;

public class ObjectFactory extends Child<ObjectFactory> {

	public static ObjectFactory newInstance()
	{
		return new ObjectFactory();
	}

	private final Bindings bindings;
	private final ImplementationFactory implementationFactory = new ImplementationFactory();

	private ObjectFactory()
	{
		this.bindings = new Bindings();
	}

	private ObjectFactory(ObjectFactory parent)
	{
		super(parent);

		this.bindings = new Bindings(parent.bindings);
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
			implementation = this.tryToCreateImplementation(request);

			if (implementation == null)
			{
				return null;
			}
		}

		return Try.to(implementation::newInstance);
	}

	private Class<?> tryToCreateImplementation(Class<?> request)
	{
		Class<?> implementation = this.implementationFactory.createImplementationClass(request);

		if (implementation != null)
		{
			this.bindings.registerBinding(request, implementation);
		}

		return implementation;
	}

}