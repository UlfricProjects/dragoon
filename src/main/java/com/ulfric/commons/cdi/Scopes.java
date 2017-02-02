package com.ulfric.commons.cdi;

import com.ulfric.commons.cdi.construct.InstanceUtils;
import com.ulfric.commons.cdi.scope.DefaultScopeStrategy;
import com.ulfric.commons.cdi.scope.Scope;
import com.ulfric.commons.cdi.scope.ScopeNotPresentException;
import com.ulfric.commons.cdi.scope.ScopeStrategy;
import com.ulfric.commons.cdi.scope.Scoped;
import com.ulfric.commons.reflect.AnnotationUtils;

import java.lang.annotation.Annotation;

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
		ScopeStrategy scope = this.registered.computeIfAbsent(request, this::resolveScopeType);
		Scoped<T> scoped = scope.getOrEmpty(request);
		if (scoped.isEmpty() && this.hasParent())
		{
			scoped = getParent().getScopedObject(request);
		}
		return scoped;
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
		
		this.registered.put(request, (ScopeStrategy) InstanceUtils.createOrNullArgs(implementation, this));
	}

}