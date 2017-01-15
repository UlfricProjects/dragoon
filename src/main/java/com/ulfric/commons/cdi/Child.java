package com.ulfric.commons.cdi;

class Child<T> {

	private final T parent;

	Child()
	{
		this(null);
	}

	Child(T parent)
	{
		this.parent = parent;
	}

	final boolean hasParent()
	{
		return this.parent != null;
	}

	final T getParent()
	{
		return this.parent;
	}

	T createChild()
	{
		return this.parent;
	}

}