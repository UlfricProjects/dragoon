package com.ulfric.commons.cdi.intercept;

public interface Interceptor<T> {

	T intercept(Context<T> context);

}