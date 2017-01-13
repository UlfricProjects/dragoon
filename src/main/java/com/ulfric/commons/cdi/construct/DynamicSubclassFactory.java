package com.ulfric.commons.cdi.construct;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import com.ulfric.commons.cdi.intercept.BytebuddyInterceptor;
import com.ulfric.commons.cdi.intercept.InterceptorPipeline;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

final class DynamicSubclassFactory<T> {

	private final BeanFactory factory;
	private final Class<T> parent;
	private DynamicType.Builder<? extends T> builder;

	public DynamicSubclassFactory(BeanFactory factory, Class<T> parent)
	{
		this.factory = factory;
		this.parent = parent;
	}

	public Class<? extends T> create()
	{
		if (!this.canBeIntercepted())
		{
			return this.parent;
		}

		this.createBuilder();
		this.addInterceptorsToMethods();
		return this.createFromBuilder();
	}

	private boolean canBeIntercepted()
	{
		Class<?> clazz = this.parent;
		return !clazz.isInterface() &&
				!clazz.isEnum() &&
				!this.isAbstractOrFinal();
	}

	private boolean isAbstractOrFinal()
	{
		int modifiers = this.parent.getModifiers();
		return Modifier.isAbstract(modifiers) || Modifier.isFinal(modifiers);
	}

	private void createBuilder()
	{
		this.builder = this.startSubclass(this.parent);
	}

	private void addInterceptorsToMethods()
	{
		Arrays.stream(this.parent.getMethods())
			.forEach(this::createInterceptorsForMethod);
	}

	private Class<? extends T> createFromBuilder()
	{
		return this.builder.make().load(this.getParentLoader()).getLoaded();
	}

	private ClassLoader getParentLoader()
	{
		return this.parent.getClassLoader();
	}

	private void createInterceptorsForMethod(Method method)
	{
		InterceptorPipeline pipeline = this.createInterceptorFactory(method).create();
		if (pipeline != null)
		{
			this.builder = this.builder.method(ElementMatchers.is(method))
					.intercept(MethodDelegation.to(BytebuddyInterceptor.newInstance(pipeline)))
					.annotateMethod(method.getAnnotations());
		}
	}

	private InterceptorFactory createInterceptorFactory(Method method)
	{
		return new InterceptorFactory(this.factory, method);
	}

	private DynamicType.Builder<? extends T> startSubclass(Class<T> parent)
	{
		return new ByteBuddy()
				.subclass(parent)
				.annotateType(parent.getAnnotations());
	}

}