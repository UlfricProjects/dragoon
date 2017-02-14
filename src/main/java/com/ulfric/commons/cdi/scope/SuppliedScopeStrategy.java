package com.ulfric.commons.cdi.scope;


import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class SuppliedScopeStrategy implements ScopeStrategy {

	private final Map<Class<?>, Supplier<?>> objectSuppliers = new IdentityHashMap<>();

	protected SuppliedScopeStrategy()
	{

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

		if (supplier == null)
		{
			throw new EmptyScopeException("Cannot get value for " + request.getName());
		}
		else
		{
			@SuppressWarnings("unchecked")
			T object = (T) supplier.get();
			return new Scoped<>(object);
		}
	}

	@Override
	public <T> Scoped<T> getOrEmpty(Class<T> request)
	{
		Supplier<?> supplier = this.objectSuppliers.get(request);

		if (supplier == null)
		{
			return Scoped.createEmptyScope();
		}
		else
		{
			@SuppressWarnings("unchecked")
			T object = (T) supplier.get();
			return new Scoped<>(object);
		}
	}

	public class EmptyScopeException extends RuntimeException {

		public EmptyScopeException(String message)
		{
			super(message);
		}
	}
}