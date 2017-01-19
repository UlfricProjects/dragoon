package com.ulfric.commons.cdi;

import com.ulfric.commons.cdi.construct.InstanceUtils;
import com.ulfric.commons.cdi.scope.ScopeStrategy;

final class Scopes extends Registry<Scopes, ScopeStrategy> {

	Scopes()
	{
		
	}

	Scopes(Scopes parent)
	{
		super(parent);
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