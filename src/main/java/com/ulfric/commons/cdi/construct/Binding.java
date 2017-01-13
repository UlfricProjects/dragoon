package com.ulfric.commons.cdi.construct;

import java.util.Objects;

import com.ulfric.commons.cdi.construct.scope.ScopeStrategy;
import com.ulfric.commons.cdi.intercept.Interceptor;

public final class Binding<T> {

	static <T> Binding<T> newInstance(BeanFactory registerTo, Class<T> request)
	{
		Objects.requireNonNull(registerTo);
		Objects.requireNonNull(request);
		return new Binding<>(registerTo, request);
	}

	private final BeanFactory registerTo;
	private final Class<T> request;

	private Binding(BeanFactory registerTo, Class<T> request)
	{
		this.registerTo = registerTo;
		this.request = request;
	}

	public void to(Class<? extends T> implementation)
	{
		Objects.requireNonNull(implementation);
		this.registerTo.registerBinding(this.request, implementation);
	}

	public void toScope(Class<? extends ScopeStrategy<?>> interceptor)
	{
		Objects.requireNonNull(interceptor);
		this.registerTo.registerScopeBinding(this.request, interceptor);
	}

	public void toInterceptor(Class<? extends Interceptor> interceptor)
	{
		Objects.requireNonNull(interceptor);
		this.registerTo.registerBinding(this.request, interceptor);
	}

}