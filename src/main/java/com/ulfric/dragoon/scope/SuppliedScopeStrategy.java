package com.ulfric.dragoon.scope;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class SuppliedScopeStrategy implements ScopeStrategy {

	private final Map<Class<?>, Supplier<?>> objectSuppliers = new IdentityHashMap<>();

	public <T> void register(Class<? extends T> request, Supplier<T> supplier)
	{
		Objects.requireNonNull(request);
		Objects.requireNonNull(supplier);

		this.objectSuppliers.put(request, supplier);
	}

	@Override
	public <T> Scoped<T> getOrCreate(Class<T> request)
	{
		return this.getOrEmpty(request);
	}

	@Override
	public <T> Scoped<T> getOrEmpty(Class<T> request)
	{
		Supplier<?> supplier = this.objectSuppliers.get(request);

		if (supplier == null)
		{
			return Scoped.createEmptyScope(request);
		}
		@SuppressWarnings("unchecked")
		T object = (T) supplier.get();
		return new Scoped<>(request, object);
	}

}