package com.ulfric.dragoon;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.ulfric.dragoon.intercept.Intercept;
import com.ulfric.dragoon.intercept.Interceptor;
import com.ulfric.commons.reflect.AnnotationUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

final class DynamicSubclassBuilder<T> {

	private final ObjectFactory factory;
	private final Class<T> parent;
	private DynamicType.Builder<T> builder;
	private boolean overloadedMethods;

	DynamicSubclassBuilder(ObjectFactory factory, Class<T> parent)
	{
		this.factory = factory;
		this.parent = parent;
		this.builder = this.createNewBuilder();
	}

	private DynamicType.Builder<T> createNewBuilder()
	{
		return new ByteBuddy()
				.subclass(this.parent)
				.implement(Dynamic.class);
	}

	Class<? extends T> build()
	{
		this.make();

		if (!this.overloadedMethods)
		{
			return this.parent;
		}

		return this.builder.make().load(this.getParentLoader()).getLoaded();
	}

	private ClassLoader getParentLoader()
	{
		return this.parent.getClassLoader();
	}

	private void make()
	{
		this.addAnnotationsFromParent();
		this.overloadInterceptableMethods();
	}

	private void addAnnotationsFromParent()
	{
		this.builder = this.builder.annotateType(this.getParentAnnotations());
	}

	private Annotation[] getParentAnnotations()
	{
		return this.parent.getAnnotations();
	}

	private void overloadInterceptableMethods()
	{
		for (Method method : this.parent.getMethods())
		{
			if (!this.isOverridable(method))
			{
				continue;
			}

			List<Interceptor> interceptors = this.getInterceptors(method);

			if (interceptors.isEmpty())
			{
				continue;
			}

			this.overloadedMethods = true;

			BytebuddyInterceptor pipeline = BytebuddyInterceptor.newInstance(interceptors);
			this.builder = this.builder.method(ElementMatchers.is(method))
				.intercept(MethodDelegation.to(pipeline))
				.annotateMethod(method.getAnnotations());
		}
	}

	private boolean isOverridable(Method method)
	{
		int modifier = method.getModifiers();
		return !Modifier.isFinal(modifier) && !Modifier.isStatic(modifier);
	}

	private List<Interceptor> getInterceptors(Method method)
	{
		List<Interceptor> interceptors = new ArrayList<>();

		interceptors.addAll(this.getDirectInterceptors(method));
		this.getSuperMethods(method).stream().map(this::getDirectInterceptors).forEach(interceptors::addAll);

		return interceptors;
	}

	private List<Interceptor> getDirectInterceptors(Method method)
	{
		return AnnotationUtils.getLeafAnnotations(method, Intercept.class)
				.stream()
				.map(Annotation::annotationType)
				.map(this.factory::request)
				.filter(Interceptor.class::isInstance)
				.map(o -> (Interceptor) o)
				.collect(Collectors.toList());
	}

	private Set<Method> getSuperMethods(Method method)
	{
		return MethodUtils.getOverrideHierarchy(method, Interfaces.INCLUDE);
	}

}