package com.ulfric.dragoon;

import java.util.Objects;

import com.ulfric.commons.naming.Name;
import com.ulfric.commons.service.Service;
import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.interceptors.Asynchronous;
import com.ulfric.dragoon.interceptors.AsynchronousInterceptor;
import com.ulfric.dragoon.interceptors.Audit;
import com.ulfric.dragoon.interceptors.AuditInterceptor;
import com.ulfric.dragoon.interceptors.InitializeInterceptor;
import com.ulfric.dragoon.scope.Default;
import com.ulfric.dragoon.scope.DefaultScopeStrategy;
import com.ulfric.dragoon.scope.Scope;
import com.ulfric.dragoon.scope.Scoped;
import com.ulfric.dragoon.scope.Shared;
import com.ulfric.dragoon.scope.SharedScopeStrategy;
import com.ulfric.dragoon.scope.Supplied;
import com.ulfric.dragoon.scope.SuppliedScopeStrategy;

@Name("ObjectFactory")
@Supplied
public final class ObjectFactory extends Child<ObjectFactory> implements Factory, Service {

	public static ObjectFactory newInstance()
	{
		return new ObjectFactory();
	}

	private final Bindings bindings;
	private final Scopes scopes;
	private final Subclasser implementationFactory = new Subclasser(this);
	private final Injector injector = new Injector(this);
	private final Initializer initializer = new Initializer();

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
		this.scopes.registerBinding(Shared.class, SharedScopeStrategy.class);
		this.scopes.registerBinding(Supplied.class, SuppliedScopeStrategy.class);

		this.bindings.registerBinding(Audit.class, AuditInterceptor.class);

		this.bindings.registerBinding(Initialize.class, InitializeInterceptor.class);
		this.bindings.registerBinding(Asynchronous.class, AsynchronousInterceptor.class);

		SuppliedScopeStrategy strategy = (SuppliedScopeStrategy) this.request(Supplied.class);
		strategy.register(ObjectFactory.class, this::createChild);
	}

	@Override
	public Binding bind(Class<?> request)
	{
		Objects.requireNonNull(request);

		if (this.isScope(request))
		{
			return this.scopes.createBinding(request);
		}

		return this.bindings.createBinding(request);
	}

	private boolean isScope(Class<?> request)
	{
		return request.isAnnotation() && request.isAnnotationPresent(Scope.class);
	}

	@Override
	public <T> T requestExact(Class<T> request)
	{
		Object value = this.request(request);

		if (!request.isInstance(value))
		{
			throw new IllegalArgumentException("Wrong request type");
		}

		@SuppressWarnings("unchecked")
		T casted = (T) value;
		return casted;
	}

	@Override
	public Object request(Class<?> request)
	{
		Objects.requireNonNull(request);

		Class<?> implementation = this.bindings.getRegisteredBinding(request);

		if (implementation == null)
		{
			implementation = this.tryToCreateAndRegisterImplementation(request);

			if (implementation == null)
			{
				if (this.couldBeScope(request))
				{
					return this.scopes.getScope(request);
				}

				return null;
			}
		}

		return this.getInjectedObject(implementation);
	}

	private boolean couldBeScope(Class<?> request)
	{
		return request.isAnnotation();
	}

	private Object getInjectedObject(Class<?> implementation)
	{
		Scoped<?> scoped = this.scopes.getScopedObject(implementation);
		this.injector.injectFields(scoped);
		this.initializer.initializeScoped(scoped);
		return scoped.read();
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