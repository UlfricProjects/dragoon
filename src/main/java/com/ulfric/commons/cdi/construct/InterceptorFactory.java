package com.ulfric.commons.cdi.construct;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.ulfric.commons.cdi.intercept.FauxInterceptorException;
import com.ulfric.commons.cdi.intercept.Intercept;
import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.commons.cdi.intercept.InterceptorPipeline;
import com.ulfric.commons.reflect.AnnotationUtils;

final class InterceptorFactory {

	private final BeanFactory factory;
	private final Method intercepted;
	private final Map<Class<? extends Annotation>, Annotation> interceptors = new LinkedHashMap<>();

	public InterceptorFactory(BeanFactory factory, Method intercepted)
	{
		this.factory = factory;
		this.intercepted = intercepted;
	}

	public InterceptorPipeline create()
	{
		this.addPrimaryInterceptors();
		this.addInheritedInterceptors();

		if (!this.foundInterceptors())
		{
			return null;
		}

		return this.createPipeline();
	}

	private void addPrimaryInterceptors()
	{
		this.getDirectInterceptorAnnotations()
			.forEach(this::forceRegisterInterceptor);
	}

	private Stream<Annotation> getDirectInterceptorAnnotations()
	{
		return AnnotationUtils.getLeafAnnotations(this.intercepted, Intercept.class).stream();
	}

	private void forceRegisterInterceptor(Annotation annotation)
	{
		this.interceptors.put(annotation.annotationType(), annotation);
	}

	private void addInheritedInterceptors()
	{
		this.superMethods(this.intercepted)
			.flatMap(this::getInheritedInterceptorAnnotations)
			.forEach(this::registerInterceptorIfAbsent);
	}

	private void registerInterceptorIfAbsent(Annotation annotation)
	{
		this.interceptors.putIfAbsent(annotation.annotationType(), annotation);
	}

	private boolean foundInterceptors()
	{
		return !this.interceptors.isEmpty();
	}

	private InterceptorPipeline createPipeline()
	{
		InterceptorPipeline.Builder pipeline = InterceptorPipeline.builder();
		this.getInterceptorImplementations()
			.forEach(pipeline::addInterceptor);
		return pipeline.build();
	}

	private Stream<Interceptor> getInterceptorImplementations()
	{
		return this.interceptors.values()
				.stream()
				.map(this::getInterceptorImplementation);
	}

	private Interceptor getInterceptorImplementation(Annotation interceptor)
	{
		Object interceptorImpl = this.factory.request(interceptor.annotationType());
		this.verifyObjectIsInterceptor(interceptorImpl);
		return (Interceptor) interceptorImpl;
	}

	private void verifyObjectIsInterceptor(Object suspect)
	{
		if (suspect instanceof Interceptor)
		{
			return;
		}

		throw new FauxInterceptorException(suspect);
	}

	private Stream<Method> superMethods(Method method)
	{
		return MethodUtils.getOverrideHierarchy(method, Interfaces.INCLUDE).stream();
	}

	private Stream<Annotation> getInheritedInterceptorAnnotations(Method method)
	{
		return AnnotationUtils.getLeafAnnotations(method, Intercept.class)
				.stream()
				.filter(this::isInherited);
	}

	private boolean isInherited(Annotation annotation)
	{
		return annotation.annotationType().isAnnotationPresent(Inherited.class);
	}

}