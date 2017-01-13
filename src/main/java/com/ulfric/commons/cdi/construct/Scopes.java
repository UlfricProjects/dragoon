package com.ulfric.commons.cdi.construct;

import java.lang.annotation.Annotation;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ulfric.commons.cdi.construct.scope.Default;
import com.ulfric.commons.cdi.construct.scope.DefaultImpl;
import com.ulfric.commons.cdi.construct.scope.Scope;
import com.ulfric.commons.cdi.construct.scope.ScopeStrategy;
import com.ulfric.commons.reflect.AnnotationUtils;

final class ScopeMappings {

	private final ScopeMappings parent;
	private final Map<Class<?>, ScopeStrategy<? extends Annotation>> scopeStrategies = new IdentityHashMap<>();
	private final Map<Class<?>, Class<? extends Annotation>> scopeTypes = new IdentityHashMap<>();
	private final ReadWriteLock scopeStrategiesLock = new ReentrantReadWriteLock();
	private final ReadWriteLock scopeTypesLock = new ReentrantReadWriteLock();

	public ScopeMappings(ScopeMappings parent)
	{
		this.parent = parent;
	}

	public <T extends Annotation> ScopeStrategy<T> getScopeStrategy(Class<T> scope)
	{
		this.scopeStrategiesLock.readLock().lock();

		@SuppressWarnings("unchecked")
		ScopeStrategy<T> strategy = (ScopeStrategy<T>) this.scopeStrategies.get(scope);
		if (strategy == null && this.hasParent())
		{
			strategy = this.parent.getScopeStrategy(scope);
		}

		this.scopeStrategiesLock.readLock().unlock();

		return strategy;
	}

	void registerScopeStrategy(Class<?> request, ScopeStrategy<? extends Annotation> implementation)
	{
		this.scopeStrategiesLock.writeLock().lock();

		this.scopeStrategies.put(request, implementation);

		this.scopeStrategiesLock.writeLock().unlock();
	}

	Annotation getScopeForType(Class<?> holder)
	{
		Class<? extends Annotation> scopeType = this.getRecursiveScopeFromCaches(holder);

		if (scopeType == null)
		{
			scopeType = this.resolveAndPutScopeType(holder);
		}

		Annotation scope = holder.getAnnotation(scopeType);

		return scope == null ? DefaultImpl.INSTANCE : scope;
	}

	private Class<? extends Annotation> getRecursiveScopeFromCaches(Class<?> request)
	{
		this.scopeTypesLock.readLock().lock();

		Class<? extends Annotation> binding = this.scopeTypes.get(request);
		if (binding == null && this.hasParent())
		{
			binding = this.parent.getRecursiveScopeFromCaches(request);
		}

		this.scopeTypesLock.readLock().unlock();

		return binding;
	}

	private Class<? extends Annotation> resolveAndPutScopeType(Class<?> holder)
	{
		this.scopeTypesLock.writeLock().lock();

		Class<? extends Annotation> scopeType = this.resolveScope(holder);
		this.scopeTypes.put(holder, scopeType);

		this.scopeTypesLock.writeLock().unlock();

		return scopeType;
	}

	private Class<? extends Annotation> resolveScope(Class<?> holder)
	{
		for (Annotation scope : AnnotationUtils.getLeafAnnotations(holder, Scope.class))
		{
			Class<? extends Annotation> scopeType = scope.annotationType();
			if (this.getScopeStrategy(scopeType) != null)
			{
				return scopeType;
			}
		}
		return Default.class;
	}

	private boolean hasParent()
	{
		return this.parent != null;
	}

}