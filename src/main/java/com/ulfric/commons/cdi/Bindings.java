package com.ulfric.commons.cdi;

final class Bindings {

	private final Bindings parent;

	Bindings()
	{
		this(null);
	}

	Bindings(Bindings parent)
	{
		this.parent = parent;
	}

	boolean hasParent()
	{
		return this.parent != null;
	}

}