package com.ulfric.commons.cdi.construct.scope;

import com.ulfric.commons.cdi.construct.InstanceUtils;
import com.ulfric.commons.cdi.inject.Injector;

public enum NoInjectScopeStrategy implements ScopeStrategy<NoInject> {

	INSTANCE;

	@Override
	public <T> T getInstance(Class<T> request, NoInject scope, Injector injector)
	{
		T instance = InstanceUtils.getInstance(request);
		return instance;
	}

}