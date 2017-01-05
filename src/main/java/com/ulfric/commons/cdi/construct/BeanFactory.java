package com.ulfric.commons.cdi.construct;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.ulfric.commons.cdi.construct.scope.Default;
import com.ulfric.commons.cdi.construct.scope.DefaultImpl;
import com.ulfric.commons.cdi.construct.scope.DefaultScopeStrategy;
import com.ulfric.commons.cdi.construct.scope.Scope;
import com.ulfric.commons.cdi.construct.scope.ScopeNotPresentException;
import com.ulfric.commons.cdi.construct.scope.ScopeStrategy;
import com.ulfric.commons.cdi.construct.scope.Shared;
import com.ulfric.commons.cdi.construct.scope.SharedScopeStrategy;
import com.ulfric.commons.cdi.construct.scope.Supplied;
import com.ulfric.commons.cdi.construct.scope.SuppliedScopeStrategy;
import com.ulfric.commons.cdi.inject.Injector;
import com.ulfric.commons.cdi.intercept.async.Asynchronous;
import com.ulfric.commons.cdi.intercept.async.AsynchronousInterceptor;
import com.ulfric.commons.cdi.intercept.BytebuddyInterceptor;
import com.ulfric.commons.cdi.intercept.Intercept;
import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.commons.cdi.intercept.InterceptorPipeline;
import com.ulfric.commons.cdi.intercept.random.ChanceToRun;
import com.ulfric.commons.cdi.intercept.random.ChanceToRunInterceptor;
import com.ulfric.commons.collect.MapUtils;
import com.ulfric.commons.reflect.AnnotationUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

@Supplied
public final class BeanFactory {

	public static BeanFactory newInstance()
	{
		return new BeanFactory();
	}

	private BeanFactory()
	{
		this.injector = Injector.newInstance(this);
		this.bindings = MapUtils.newSynchronizedIdentityHashMap();
		this.scopes = MapUtils.newSynchronizedIdentityHashMap();
		this.scopeTypes = MapUtils.newSynchronizedIdentityHashMap();
		this.registerDefaultScopes();
		this.registerDefaultInterceptors();
		this.registerThisAsInjectable();
	}

	private void registerDefaultScopes()
	{
		this.bind(Default.class).toScope(DefaultScopeStrategy.class);
		this.bind(Shared.class).toScope(SharedScopeStrategy.class);
		this.bind(Supplied.class).toScope(SuppliedScopeStrategy.class);
	}

	private void registerDefaultInterceptors()
	{
		this.bind(Asynchronous.class).toInterceptor(AsynchronousInterceptor.class);
		this.bind(ChanceToRun.class).toInterceptor(ChanceToRunInterceptor.class);
	}

	private void registerThisAsInjectable()
	{
		this.bind(BeanFactory.class).to(BeanFactory.class);

		ScopeStrategy<? extends Annotation> scope = this.scopes.get(Supplied.class);
		SuppliedScopeStrategy strategy = (SuppliedScopeStrategy) scope;
		strategy.put(BeanFactory.class, this);
	}

	private final Injector injector;
	private final Map<Class<?>, Class<?>> bindings;
	private final Map<Class<?>, ScopeStrategy<? extends Annotation>> scopes;
	private final Map<Class<?>, Class<? extends Annotation>> scopeTypes;

	public Injector getInjector()
	{
		return this.injector;
	}

	public <T> Object request(Class<T> request)
	{
		@SuppressWarnings("unchecked")
		Class<? extends T> binding = (Class<? extends T>)
			this.bindings.computeIfAbsent(request, this::createInterceptorClass);

		if (binding == null)
		{
			throw new BindingNotPresentException(request);
		}

		Annotation scope = this.getScope(binding);
		return this.createInstance(scope, binding);
	}

	private Annotation getScope(Class<?> holder)
	{
		Class<? extends Annotation> scopeType = this.scopeTypes.computeIfAbsent(holder, this::resolveScope);

		Annotation scope = holder.getAnnotation(scopeType);
		return scope == null ? DefaultImpl.INSTANCE : scope;
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
		return Default.class;
	}

	private <T> T createInstance(Annotation scope, Class<T> request)
	{
		@SuppressWarnings("unchecked")
		ScopeStrategy<Annotation> instanceCache = (ScopeStrategy<Annotation>)
			this.scopes.get(scope.annotationType());

		if (instanceCache == null)
		{
			throw new ScopeNotPresentException(scope.annotationType());
		}

		return instanceCache.getInstance(request, scope, this.injector);
	}

	public <T> Binding<T> bind(Class<T> request)
	{
		Objects.requireNonNull(request);

		return Binding.newInstance(this, request);
	}

	void bindScope(Class<?> request, Class<? extends ScopeStrategy<?>> implementation)
	{
		ScopeStrategy<? extends Annotation> scopeStrategy = InstanceUtils.getInstance(implementation);
		this.scopes.put(request, scopeStrategy);
	}

	void bind(Class<?> request, Class<?> implementation)
	{
		Objects.requireNonNull(request);
		Objects.requireNonNull(implementation);

		Class<?> wrappedImplementation = this.createInterceptorClass(implementation);
		this.bindings.put(request, wrappedImplementation);
	}

	// TODO come up with a better name
	private Class<?> createInterceptorClass(Class<?> implementation)
	{
		if (!this.canBeIntercepted(implementation))
		{
			return implementation;
		}

		DynamicType.Builder<?> builder = new ByteBuddy().subclass(implementation);
		for (Method method : implementation.getMethods())
		{
			Map<Class<? extends Annotation>, Annotation> interceptors = new LinkedHashMap<>();
			AnnotationUtils.getLeafAnnotations(method, Intercept.class)
				.forEach(annotation -> interceptors.put(annotation.annotationType(), annotation));

			Set<Method> superMethods = MethodUtils.getOverrideHierarchy(method, Interfaces.INCLUDE);
			for (Method superMethod : superMethods)
			{
				AnnotationUtils.getLeafAnnotations(superMethod, Intercept.class)
					.stream()
					.filter(annotation -> annotation.annotationType().isAnnotationPresent(Inherited.class))
					.forEach(annotation -> interceptors.put(annotation.annotationType(), annotation));
			}

			if (interceptors.isEmpty())
			{
				continue;
			}

			InterceptorPipeline.Builder<Object> pipeline = InterceptorPipeline.builder();
			for (Annotation interceptor : interceptors.values())
			{
				Object interceptorImpl = this.request(interceptor.annotationType());

				if (!(interceptorImpl instanceof Interceptor))
				{
					// TODO throw different exception
					throw new RuntimeException();
				}

				@SuppressWarnings("unchecked")
				Interceptor<Object> casted = (Interceptor<Object>) interceptorImpl;
				pipeline.addInterceptor(casted);
			}

			builder = builder.method(ElementMatchers.is(method))
				.intercept(MethodDelegation.to(BytebuddyInterceptor.newInstance(pipeline.build())))
				.annotateMethod(method.getAnnotations());
		}
		return builder.make().load(implementation.getClassLoader()).getLoaded();
	}

	private boolean canBeIntercepted(Class<?> clazz)
	{
		return !clazz.isInterface() &&
				!clazz.isEnum() &&
				!this.isAbstractOrFinal(clazz);
	}

	private boolean isAbstractOrFinal(Class<?> clazz)
	{
		int modifiers = clazz.getModifiers();
		return Modifier.isAbstract(modifiers) || Modifier.isFinal(modifiers);
	}

}