package com.ulfric.dragoon.extension.intercept;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import com.ulfric.dragoon.Dynamic;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.reflect.Methods;
import com.ulfric.dragoon.stereotype.Stereotypes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InterceptedClassBuilder<T> {

	private final ObjectFactory factory;
	private final Class<T> parent;
	private DynamicType.Builder<T> builder;
	private boolean changed;

	public InterceptedClassBuilder(ObjectFactory factory, Class<T> parent)
	{
		this.factory = factory;
		this.parent = parent;
		this.builder = new ByteBuddy().subclass(parent);
	}

	public Class<? extends T> build()
	{
		this.run();

		if (!this.changed)
		{
			return this.parent;
		}

		return this.builder.implement(Dynamic.class)
				.make()
				.load(this.parent.getClassLoader())
				.getLoaded();
	}

	private void run()
	{
		for (Method method : Methods.getOverridableMethods(this.parent))
		{
			List<Interceptor<?>> interceptors = Stereotypes.getStereotypes(method, Intercept.class)
					.stream()
					.map(this::createInterceptor)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

			if (interceptors.isEmpty())
			{
				continue;
			}

			InterceptorPipeline pipeline = new InterceptorPipeline(interceptors);

			this.builder = this.builder.method(ElementMatchers.is(method))
					.intercept(MethodDelegation.to(pipeline))
					.annotateMethod(method.getDeclaredAnnotations());

			this.changed = true;
		}
	}

	private Interceptor<?> createInterceptor(Annotation annotation)
	{
		return (Interceptor<?>) this.factory.requestUnchecked(annotation.annotationType(), annotation);
	}

}