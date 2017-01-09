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
import com.ulfric.commons.cdi.intercept.BytebuddyInterceptor;
import com.ulfric.commons.cdi.intercept.FauxInterceptorException;
import com.ulfric.commons.cdi.intercept.Intercept;
import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.commons.cdi.intercept.InterceptorPipeline;
import com.ulfric.commons.cdi.intercept.async.Asynchronous;
import com.ulfric.commons.cdi.intercept.async.AsynchronousInterceptor;
import com.ulfric.commons.cdi.intercept.random.ChanceToRun;
import com.ulfric.commons.cdi.intercept.random.ChanceToRunInterceptor;
import com.ulfric.commons.collect.MapUtils;
import com.ulfric.commons.naming.Name;
import com.ulfric.commons.reflect.AnnotationUtils;
import com.ulfric.commons.service.Service;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

@Supplied
@Name("BeanFactory")
public final class BeanFactory implements Service {

	public static BeanFactory newInstance()
	{
		return BeanFactory.newInstance(null);
	}

	public static BeanFactory newInstance(BeanFactory parent)
	{
		return new BeanFactory(parent);
	}

	private BeanFactory(BeanFactory parent)
	{
		this.parent = parent;
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

	private final BeanFactory parent;
	private final Injector injector;
	private final Map<Class<?>, Class<?>> bindings;
	private final Map<Class<?>, ScopeStrategy<? extends Annotation>> scopes;
	private final Map<Class<?>, Class<? extends Annotation>> scopeTypes;

	public Injector getInjector()
	{
		return this.injector;
	}

	private boolean hasParent()
	{
		return this.parent != null;
	}

	public Object request(Class<?> request)
	{
		Objects.requireNonNull(request);

		Class<?> binding = this.getRecursiveBindingWithoutCreation(request);

		if (binding == null)
		{
			binding = this.bindings.computeIfAbsent(request, this::createInterceptorClass);
		}

		Annotation scope = this.getScope(binding);
		return this.createInstance(scope, binding);
	}

	private synchronized Class<?> getRecursiveBindingWithoutCreation(Class<?> request)
	{
		Class<?> binding = this.bindings.get(request);

		if (binding == null && this.hasParent())
		{
			return this.parent.getRecursiveBindingWithoutCreation(request);
		}

		return binding;
	}

	private Annotation getScope(Class<?> holder)
	{
		Class<? extends Annotation> scopeType = this.scopeTypes.computeIfAbsent(holder, this::resolveScope);

		Annotation scope = holder.getAnnotation(scopeType);

		if (scope == null && this.parent != null)
		{
			return this.parent.getScope(holder);
		}

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

	private Class<?> createInterceptorClass(Class<?> implementation)
	{
		if (!this.canBeIntercepted(implementation))
		{
			return implementation;
		}

		DynamicType.Builder<?> builder = new ByteBuddy()
				.subclass(implementation)
				.annotateType(implementation.getAnnotations());

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

			InterceptorPipeline.Builder pipeline = InterceptorPipeline.builder();
			for (Annotation interceptor : interceptors.values())
			{
				Object interceptorImpl = this.request(interceptor.annotationType());

				if (!(interceptorImpl instanceof Interceptor))
				{
					throw new FauxInterceptorException(interceptorImpl);
				}

				Interceptor casted = (Interceptor) interceptorImpl;
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