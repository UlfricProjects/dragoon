package com.ulfric.commons.cdi.intercept;

public interface Interceptor {

	Object intercept(Invocation invocation);

}