package com.ulfric.commons.cdi;

import java.util.Objects;

public final class Binding {

	private final Registry<?> registerTo;
	private final Class<?> request;

	Binding(Registry<?> registerTo, Class<?> request)
	{
		this.registerTo = registerTo;
		this.request = request;
	}

	public void to(Class<?> implementation)
	{
		Objects.requireNonNull(implementation);

		this.registerTo.registerBinding(this.request, implementation);
	}

}