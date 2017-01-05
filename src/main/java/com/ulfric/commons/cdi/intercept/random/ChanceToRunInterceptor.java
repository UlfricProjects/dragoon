package com.ulfric.commons.cdi.intercept.random;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Interceptor;

public class ChanceToRunInterceptor implements Interceptor {

	@Override
	public Object intercept(Context context)
	{
		double percent = context.getOrigin().getDeclaredAnnotation(ChanceToRun.class).value();

		double rand = ThreadLocalRandom.current().nextDouble();

		if (rand > percent)
		{
			return Optional.empty();
		}

		return Optional.ofNullable(context.proceed());
	}

}
