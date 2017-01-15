package com.ulfric.commons.cdi;

import java.util.Objects;

public final class Binding<T> {

	private final Bindings registerTo;
	private final Class<T> request;

	Binding(Bindings registerTo, Class<T> request)
	{
		this.registerTo = registerTo;
		this.request = request;
	}

	public void to(Class<? extends T> implementation)
	{
		Objects.requireNonNull(implementation);

		this.registerTo.registerBinding(this.request, implementation);
	}

}