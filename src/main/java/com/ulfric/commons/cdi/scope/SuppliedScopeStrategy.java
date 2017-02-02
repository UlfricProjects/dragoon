package com.ulfric.commons.cdi.scope;

import com.ulfric.commons.cdi.Scopes;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class SuppliedScopeStrategy extends ScopeStrategy {

	private final Map<Class<?>, Supplier<?>> objectSuppliers = new IdentityHashMap<>();
	
	protected SuppliedScopeStrategy(Scopes parent)
	{
		super(parent);
	}
	
	protected SuppliedScopeStrategy()
	{
		super(null);
	}
	
	public <T> void register(Class<T> request, Supplier<T> supplier)
	{
		Objects.requireNonNull(request);
		Objects.requireNonNull(supplier);

		this.objectSuppliers.put(request, supplier);
	}

	@Override
	public <T> Scoped<T> getOrCreate(Class<T> request)
	{
		Supplier<?> supplier = this.objectSuppliers.get(request);
		
		// TODO: 2/2/2017 This should maybe throw an error provided it has the same functionality as #getOrEmpty 
		if (supplier == null)
		{
			return Scoped.createEmptyScope(request);
		}

		@SuppressWarnings("unchecked")
		T object = (T) supplier.get();
		return new Scoped<>(request, object);
	}
	
	@Override
	public <T> Scoped<T> getOrEmpty(Class<T> request)
	{
		return getOrCreate(request);
	}
	
}