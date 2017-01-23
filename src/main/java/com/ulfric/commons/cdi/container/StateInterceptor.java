package com.ulfric.commons.cdi.container;

import java.util.logging.Logger;

import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.commons.naming.Named;

abstract class StateInterceptor implements Interceptor {

	@Inject
	protected Logger logger;

	@Override
	public Object intercept(Context context)
	{
		Object owner = context.getOwner();
		String name = this.getName(owner);

		this.before(name);
		long start = System.currentTimeMillis();
		Object result = context.proceed();
		long end = System.currentTimeMillis();
		this.after(name, end - start);

		return result;
	}

	private String getName(Object object)
	{
		if (object instanceof Named)
		{
			return ((Named) object).getName();
		}

		return String.valueOf(object);
	}

	protected abstract void before(String name);

	protected abstract void after(String name, long timeToProcessInMillis);

}