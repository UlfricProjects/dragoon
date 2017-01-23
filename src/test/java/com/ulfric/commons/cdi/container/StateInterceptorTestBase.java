package com.ulfric.commons.cdi.container;

import java.lang.reflect.Field;

import com.ulfric.commons.exception.Try;
import com.ulfric.verify.Verify;

abstract class StateInterceptorTestBase<T extends StateInterceptor> {

	private final T t;

	StateInterceptorTestBase(T t)
	{
		this.t = t;
		Try.to(() ->
		{
			Field field = t.getClass().getSuperclass().getDeclaredField("logger");

			field.set(this.t, new NullLogger());
		});
	}

	void before_noExceptions()
	{
		Verify.that(() -> this.t.before("")).runsWithoutExceptions();
	}

	void after_noExceptions()
	{
		Verify.that(() -> this.t.after("", 0L)).runsWithoutExceptions();
	}

}
