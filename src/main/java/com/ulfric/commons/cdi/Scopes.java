package com.ulfric.commons.cdi;

import java.lang.annotation.Annotation;
import java.util.IdentityHashMap;
import java.util.Map;

import com.ulfric.commons.cdi.construct.InstanceUtils;
import com.ulfric.commons.cdi.scope.DefaultScopeStrategy;
import com.ulfric.commons.cdi.scope.Scope;
import com.ulfric.commons.cdi.scope.ScopeNotPresentException;
import com.ulfric.commons.cdi.scope.ScopeStrategy;
import com.ulfric.commons.cdi.scope.Scoped;
import com.ulfric.commons.reflect.AnnotationUtils;

final class Scopes extends Registry<Scopes, ScopeStrategy> {

	private final Map<Class<?>, ScopeStrategy> scopedTypes = new IdentityHashMap<>();

	Scopes()
	{
		
	}

	Scopes(Scopes parent)
	{
		super(parent);
	}

	public <T> Scoped<T> getScopedObject(Class<T> request)
	{
		ScopeStrategy scope = this.scopedTypes.computeIfAbsent(request, this::resolveScopeType);
		return scope.getOrCreate(request);
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

		this.registered.put(request, (ScopeStrategy) InstanceUtils.createOrNull(implementation));
	}

}