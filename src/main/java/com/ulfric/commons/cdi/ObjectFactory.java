package com.ulfric.commons.cdi;

import org.apache.commons.lang3.Validate;

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

	public void bind(Object object)
	{
		Validate.notNull(object);
	}

}