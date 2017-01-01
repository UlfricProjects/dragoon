package com.ulfric.commons.cdi.construct.scope;

import com.ulfric.commons.cdi.construct.InstanceUtils;
import com.ulfric.commons.cdi.inject.Injector;

public enum DefaultScopeStrategy implements ScopeStrategy<Default> {

	INSTANCE;

	@Override
	public <T> T getInstance(Class<T> request, Default scope, Injector injector)
	{
		T instance = InstanceUtils.getInstance(request);
		if (instance != null)
		{
			injector.injectState(instance);
		}
		return instance;
	}

}