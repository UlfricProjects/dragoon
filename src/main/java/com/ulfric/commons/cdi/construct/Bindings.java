package com.ulfric.commons.cdi.construct;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class Bindings {

	private final Bindings parent;
	private final Map<Class<?>, Class<?>> bindings = new IdentityHashMap<>();
	private final ReadWriteLock bindingsLock = new ReentrantReadWriteLock();

	public Bindings(Bindings parent)
	{
		this.parent = parent;
	}

	private boolean hasParent()
	{
		return this.parent != null;
	}

	void registerBinding(Class<?> request, Class<?> impl)
	{
		this.bindingsLock.writeLock().lock();

		this.bindings.put(request, impl);

		this.bindingsLock.writeLock().unlock();
	}

	public Class<?> getBinding(Class<?> request)
	{
		return this.getRecursiveBindingFromCaches(request);
	}

	private Class<?> getRecursiveBindingFromCaches(Class<?> request)
	{
		this.bindingsLock.readLock().lock();

		Class<?> binding = this.bindings.get(request);
		if (binding == null && this.hasParent())
		{
			binding = this.parent.getRecursiveBindingFromCaches(request);
		}

		this.bindingsLock.readLock().unlock();
		return binding;
	}

}