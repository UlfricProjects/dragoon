package com.ulfric.commons.cdi;

import java.util.Objects;

public class ObjectFactory {

	public static ObjectFactory newInstance()
	{
		return new ObjectFactory();
	}

	private final ObjectFactory parent;

	private ObjectFactory()
	{
		this(null);
	}

	private ObjectFactory(ObjectFactory parent)
	{
		this.parent = parent;
	}

	ObjectFactory subfactory()
	{
		return new ObjectFactory(this);
	}

	boolean hasParent()
	{
		return this.parent != null;
	}

	public <T> Binding<T> bind(Class<T> request)
	{
		Objects.requireNonNull(request);

		return new Binding<>(request);
	}

}