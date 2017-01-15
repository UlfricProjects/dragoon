package com.ulfric.commons.cdi;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class Bindings extends Child<Bindings> {

	private final Map<Class<?>, Class<?>> bindings = new IdentityHashMap<>();
	private final ReadWriteLock mutex = new ReentrantReadWriteLock();

	Bindings()
	{
		
	}

	Bindings(Bindings parent)
	{
		super(parent);
	}

	<T> Binding<T> createBinding(Class<T> request)
	{
		return new Binding<>(this, request);
	}

	Class<?> getRegisteredBinding(Class<?> request)
	{
		this.mutex.readLock().lock();

		Class<?> binding = this.bindings.get(request);

		if (binding == null && this.hasParent())
		{
			binding = this.getParent().getRegisteredBinding(request);
		}

		this.mutex.readLock().unlock();
		return binding;
	}

	void registerBinding(Class<?> request, Class<?> implementation)
	{
		this.mutex.writeLock().lock();

		this.bindings.put(request, implementation);

		this.mutex.writeLock().unlock();
	}

}