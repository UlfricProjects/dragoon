package com.ulfric.commons.cdi.construct;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

import com.ulfric.commons.cdi.construct.scope.Default;
import com.ulfric.commons.cdi.construct.scope.DefaultImpl;
import com.ulfric.commons.cdi.construct.scope.DefaultScopeStrategy;
import com.ulfric.commons.cdi.construct.scope.Scope;
import com.ulfric.commons.cdi.construct.scope.ScopeNotPresentException;
import com.ulfric.commons.cdi.construct.scope.ScopeStrategy;
import com.ulfric.commons.cdi.construct.scope.Shared;
import com.ulfric.commons.cdi.construct.scope.SharedScopeStrategy;
import com.ulfric.commons.cdi.inject.Injector;
import com.ulfric.commons.cdi.intercept.async.Asynchronous;
import com.ulfric.commons.cdi.intercept.async.AsynchronousInterceptor;
import com.ulfric.commons.cdi.intercept.random.ChanceToRun;
import com.ulfric.commons.cdi.intercept.random.ChanceToRunInterceptor;
import com.ulfric.commons.collect.MapUtils;
import com.ulfric.commons.naming.Name;
import com.ulfric.commons.reflect.AnnotationUtils;
import com.ulfric.commons.service.Service;

@Name("BeanFactory")
public class BeanFactory implements Service {

	public static BeanFactory newInstance()
	{
		return new BeanFactory(null);
	}

	private final Injector injector;
	private final Map<Class<?>, Class<?>> bindings;
	private final Map<Class<?>, ScopeStrategy<? extends Annotation>> scopes;
	private final Map<Class<?>, Class<? extends Annotation>> scopeTypes;
	private final BeanFactory parent;

	private BeanFactory(BeanFactory parent)
	{
		this.parent = parent;
		this.injector = Injector.newInstance(this);
		this.bindings = MapUtils.newSynchronizedIdentityHashMap();
		this.scopes = MapUtils.newSynchronizedIdentityHashMap();
		this.scopeTypes = MapUtils.newSynchronizedIdentityHashMap();
		this.registerDefaultScopes();
		this.registerDefaultInterceptors();
		this.bindThisManuallyToPreventDynamicSubclassingWithoutFinal();
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
		this.bindings.put(thiz, thiz);
	}

	public Injector getInjector()
	{
		return this.injector;
	}

	boolean hasParent()
	{
		return this.parent != null;
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

		Class<?> binding = this.getRecursiveBindingWithoutCreation(request);

		if (binding == null)
		{
			binding = this.bindings.computeIfAbsent(request, this::createInterceptableClass);
		}

		Annotation scope = this.getScope(binding);
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

	private Class<?> getRecursiveBindingWithoutCreation(Class<?> request)
	{
		synchronized (this.bindings)
		{
			Class<?> binding = this.bindings.get(request);

			if (binding == null && this.hasParent())
			{
				return this.parent.getRecursiveBindingWithoutCreation(request);
			}

			return binding;
		}
	}

	private Annotation getScope(Class<?> holder)
	{
		Class<? extends Annotation> scopeType = this.getRecursiveScopeWithoutCreation(holder);

		if (scopeType == null)
		{
			scopeType = this.scopeTypes.computeIfAbsent(holder, this::resolveScope);
		}

		Annotation scope = holder.getAnnotation(scopeType);

		return scope == null ? DefaultImpl.INSTANCE : scope;
	}

	private Class<? extends Annotation> getRecursiveScopeWithoutCreation(Class<?> request)
	{
		synchronized (this.scopeTypes)
		{
			Class<? extends Annotation> binding = this.scopeTypes.get(request);

			if (binding == null && this.hasParent())
			{
				return this.parent.getRecursiveScopeWithoutCreation(request);
			}

			return binding;
		}
	}

	private Class<? extends Annotation> resolveScope(Class<?> holder)
	{
		for (Annotation scope : AnnotationUtils.getLeafAnnotations(holder, Scope.class))
		{
			Class<? extends Annotation> scopeType = scope.annotationType();
			if (this.scopes.containsKey(scopeType))
			{
				return scopeType;
			}
		}
		if (this.parent != null)
		{
			return this.parent.resolveScope(holder);
		}
		return Default.class;
	}

	private <T> T createInstance(Annotation scope, Class<T> request)
	{
		@SuppressWarnings("unchecked")
		ScopeStrategy<Annotation> instanceCache = (ScopeStrategy<Annotation>)
			this.scopes.get(scope.annotationType());

		if (instanceCache == null)
		{
			if (this.parent != null)
			{
				return this.parent.createInstance(scope, request);
			}
			throw new ScopeNotPresentException(scope.annotationType());
		}

		return instanceCache.getInstance(request, scope, this.injector);
	}

	public <T> Binding<T> bind(Class<T> request)
	{
		Objects.requireNonNull(request);

		return Binding.newInstance(this, request);
	}

	void registerScopeBinding(Class<?> request, Class<? extends ScopeStrategy<?>> implementation)
	{
		ScopeStrategy<? extends Annotation> scopeStrategy = InstanceUtils.getInstance(implementation);
		this.scopes.put(request, scopeStrategy);
	}

	void registerBinding(Class<?> request, Class<?> implementation)
	{
		Class<?> wrappedImplementation = this.createInterceptableClass(implementation);
		this.bindings.put(request, wrappedImplementation);
	}

	private Class<?> createInterceptableClass(Class<?> parent)
	{
		return new DynamicSubclassFactory<>(this, parent).create();
	}

}