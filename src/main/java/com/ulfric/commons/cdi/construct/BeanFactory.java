package com.ulfric.commons.cdi.construct;

import java.lang.annotation.Annotation;
import java.util.Objects;

import com.ulfric.commons.cdi.construct.scope.Default;
import com.ulfric.commons.cdi.construct.scope.DefaultScopeStrategy;
import com.ulfric.commons.cdi.construct.scope.ScopeNotPresentException;
import com.ulfric.commons.cdi.construct.scope.ScopeStrategy;
import com.ulfric.commons.cdi.construct.scope.Shared;
import com.ulfric.commons.cdi.construct.scope.SharedScopeStrategy;
import com.ulfric.commons.cdi.inject.Injector;
import com.ulfric.commons.cdi.intercept.async.Asynchronous;
import com.ulfric.commons.cdi.intercept.async.AsynchronousInterceptor;
import com.ulfric.commons.cdi.intercept.random.ChanceToRun;
import com.ulfric.commons.cdi.intercept.random.ChanceToRunInterceptor;
import com.ulfric.commons.naming.Name;
import com.ulfric.commons.service.Service;

@Name("BeanFactory")
public class BeanFactory implements Service {

	public static BeanFactory newInstance()
	{
		return new BeanFactory(null);
	}

	private final BeanFactory parent;
	private final Injector injector;
	private final Scopes scopes;
	private final Bindings bindings;

	private BeanFactory(BeanFactory parent)
	{
		this.parent = parent;
		this.scopes = this.createScopeMappings();
		this.bindings = this.createBindings();
		this.injector = this.createInjector();
		this.registerDefaultScopes();
		this.registerDefaultInterceptors();
		this.bindThisManuallyToPreventDynamicSubclassingWithoutFinal();
	}

	private Scopes createScopeMappings()
	{
		return new Scopes(this.hasParent() ? this.parent.scopes : null);
	}

	private Bindings createBindings()
	{
		return new Bindings(this.hasParent() ? this.parent.bindings : null);
	}

	private Injector createInjector()
	{
		return Injector.newInstance(this);
	}

	private boolean hasParent()
	{
		return this.parent != null;
	}

	private void registerDefaultScopes()
	{
		this.bind(Default.class).toScope(DefaultScopeStrategy.class);
		this.bind(Shared.class).toScope(SharedScopeStrategy.class);
	}

	private void registerDefaultInterceptors()
	{
		this.bind(Asynchronous.class).toInterceptor(AsynchronousInterceptor.class);
		this.bind(ChanceToRun.class).toInterceptor(ChanceToRunInterceptor.class);
	}

	private void bindThisManuallyToPreventDynamicSubclassingWithoutFinal()
	{
		Class<?> thiz = this.getClass();
		this.bindings.registerBinding(thiz, thiz);
	}

	public <T> T requestExact(Class<T> request)
	{
		Object value = this.request(request);

		if (!request.isInstance(value))
		{
			throw new IllegalRequestExactException(request, value);
		}

		@SuppressWarnings("unchecked")
		T casted = (T) value;
		return casted;
	}

	public Object request(Class<?> request)
	{
		Objects.requireNonNull(request);

		if (this.isRequestingBeanFactory(request))
		{
			return this.createChild();
		}

		Class<?> binding = this.bindings.getBinding(request);

		if (binding == null)
		{
			binding = this.createInterceptableClass(binding);
			this.bindings.registerBinding(request, binding);
		}

		Annotation scope = this.scopes.getScopeForType(binding);
		return this.createInstance(scope, binding);
	}

	private boolean isRequestingBeanFactory(Class<?> request)
	{
		return request == this.getClass();
	}

	public BeanFactory createChild()
	{
		return new BeanFactory(this);
	}

	private <T, S extends Annotation> T createInstance(S scope, Class<T> request)
	{
		@SuppressWarnings("unchecked")
		ScopeStrategy<S> instanceCache = (ScopeStrategy<S>) this.scopes.getScopeStrategy(scope.annotationType());

		if (instanceCache == null)
		{
			throw new ScopeNotPresentException(scope.annotationType());
		}

		return instanceCache.getInstance(request, scope, this.injector);
	}

	public <T> Binding<T> bind(Class<T> request)
	{
		return Binding.newInstance(this, request);
	}

	void registerScope(Class<?> request, Class<? extends ScopeStrategy<?>> implementation)
	{
		ScopeStrategy<? extends Annotation> scopeStrategy = InstanceUtils.getInstance(implementation);
		this.scopes.registerScopeStrategy(request, scopeStrategy);
	}

	void registerBinding(Class<?> request, Class<?> implementation)
	{
		Class<?> wrappedImplementation = this.createInterceptableClass(implementation);
		this.bindings.registerBinding(request, wrappedImplementation);
	}

	private Class<?> createInterceptableClass(Class<?> parent)
	{
		return new DynamicSubclassFactory<>(this, parent).create();
	}

}