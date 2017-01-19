package com.ulfric.commons.cdi;

import java.lang.annotation.Annotation;
import java.util.Objects;

import com.ulfric.commons.cdi.scope.Default;
import com.ulfric.commons.cdi.scope.DefaultScopeStrategy;
import com.ulfric.commons.exception.Try;

public class ObjectFactory extends Child<ObjectFactory> {

	public static ObjectFactory newInstance()
	{
		return new ObjectFactory();
	}

	private final Bindings bindings;
	private final Scopes scopes;
	private final ImplementationFactory implementationFactory = new ImplementationFactory();

	private ObjectFactory()
	{
		this.bindings = new Bindings();
		this.scopes = new Scopes();

		this.init();
	}

	private ObjectFactory(ObjectFactory parent)
	{
		super(parent);

		this.bindings = new Bindings(parent.bindings);
		this.scopes = new Scopes(parent.scopes);

		this.init();
	}

	private void init()
	{
		this.scopes.registerBinding(Default.class, DefaultScopeStrategy.class);
	}

	public Binding bind(Class<?> request)
	{
		Objects.requireNonNull(request);

		return this.bindings.createBinding(request);
	}

	public Binding bindScope(Class<? extends Annotation> request)
	{
		Objects.requireNonNull(request);

		return this.scopes.createBinding(request);
	}

	public Object request(Class<?> request)
	{
		Objects.requireNonNull(request);

		Class<?> implementation = this.bindings.getRegisteredBinding(request);

		if (implementation == null)
		{
			implementation = this.tryToCreateAndRegisterImplementation(request);

			if (implementation == null)
			{
				return null;
			}
		}

		return Try.to(implementation::newInstance);
	}

	private Class<?> tryToCreateAndRegisterImplementation(Class<?> request)
	{
		Class<?> implementation = this.implementationFactory.createImplementationClass(request);

		if (implementation != null)
		{
			this.bindings.registerBinding(request, implementation);
		}

		return implementation;
	}

}