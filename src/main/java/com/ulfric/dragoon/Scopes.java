package com.ulfric.dragoon;

import java.lang.annotation.Annotation;

import com.ulfric.dragoon.construct.InstanceUtils;
import com.ulfric.dragoon.scope.DefaultScopeStrategy;
import com.ulfric.dragoon.scope.Scope;
import com.ulfric.dragoon.scope.ScopeNotPresentException;
import com.ulfric.dragoon.scope.ScopeStrategy;
import com.ulfric.dragoon.scope.Scoped;
import com.ulfric.commons.reflect.AnnotationUtils;

public final class Scopes extends Registry<Scopes, ScopeStrategy> {

	Scopes()
	{

	}

	Scopes(Scopes parent)
	{
		super(parent);
	}

	public <T> Scoped<T> getScopedObject(Class<T> request)
	{
		Scoped<T> scoped = this.findScopedObject(request);

		if (scoped.isEmpty())
		{
			scoped = this.createScopedObject(request);
		}

		return scoped;
	}

	private <T> Scoped<T> findScopedObject(Class<T> request)
	{
		ScopeStrategy scope = this.registered.computeIfAbsent(request, this::resolveScopeType);
		Scoped<T> scoped = scope.getOrEmpty(request);

		if (scoped.isEmpty() && this.hasParent())
		{
			scoped = this.getParent().findScopedObject(request);
		}

		return scoped;
	}

	private <T> Scoped<T> createScopedObject(Class<T> request)
	{
		ScopeStrategy scope = this.registered.computeIfAbsent(request, this::resolveScopeType);
		return scope.getOrCreate(request);
	}

	ScopeStrategy getScope(Class<?> scope)
	{
		ScopeStrategy strategy = this.registered.get(scope);

		if (strategy == null && this.hasParent())
		{
			strategy = this.getParent().getScope(scope);
		}

		return strategy;
	}

	private ScopeStrategy resolveScopeType(Class<?> request)
	{
		Class<?> notImplemented = null;
		for (Annotation potentialScope : AnnotationUtils.getLeafAnnotations(request, Scope.class))
		{
			ScopeStrategy scope = this.getRegisteredBinding(potentialScope.annotationType());

			if (scope == null)
			{
				notImplemented = potentialScope.annotationType();
				continue;
			}

			return scope;
		}

		if (notImplemented == null)
		{
			return DefaultScopeStrategy.INSTANCE;
		}

		throw new ScopeNotPresentException(notImplemented);
	}

	@Override
	void registerBinding(Class<?> request, Class<?> implementation)
	{
		if (!ScopeStrategy.class.isAssignableFrom(implementation))
		{
			throw new IllegalArgumentException(implementation + " is not a ScopeStrategy!");
		}
		ScopeStrategy strategy = (ScopeStrategy) InstanceUtils.createOrNull(implementation);
		this.registered.put(request, strategy);
	}

}