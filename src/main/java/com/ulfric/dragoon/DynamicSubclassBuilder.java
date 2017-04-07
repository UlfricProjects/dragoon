package com.ulfric.dragoon;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.ulfric.commons.bean.Bean;
import com.ulfric.commons.reflect.AnnotationUtils;
import com.ulfric.dragoon.intercept.Intercept;
import com.ulfric.dragoon.intercept.Interceptor;

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
		return new ByteBuddy().subclass(this.parent).implement(Dynamic.class);
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

			List<InterceptorAnnotationWrapper> interceptors = this.getInterceptors(method);

			if (interceptors.isEmpty())
			{
				continue;
			}

			this.overloadedMethods = true;

			Annotation[] applicableAnnotations = this.getCarriedAnnotations(method, interceptors);
			BytebuddyInterceptor pipeline = this.getPipeline(interceptors);
			this.builder = this.builder.method(ElementMatchers.is(method))
				.intercept(MethodDelegation.to(pipeline))
				.annotateMethod(applicableAnnotations);
		}
	}

	private boolean isOverridable(Method method)
	{
		int modifier = method.getModifiers();
		return !Modifier.isFinal(modifier) && !Modifier.isStatic(modifier);
	}

	private List<InterceptorAnnotationWrapper> getInterceptors(Method method)
	{
		return this.getMethodFamily(method).stream().map(this::getDirectInterceptors).flatMap(List::stream).collect(Collectors.toList());
	}

	private List<InterceptorAnnotationWrapper> getDirectInterceptors(Method method)
	{
		return AnnotationUtils.getLeafAnnotations(method, Intercept.class)
			.stream()
			.map(intercept ->
			{
				Interceptor interceptor = (Interceptor) this.factory.request(intercept.annotationType());
				return new InterceptorAnnotationWrapper(interceptor, intercept);
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	private Set<Method> getMethodFamily(Method method)
	{
		return MethodUtils.getOverrideHierarchy(method, Interfaces.INCLUDE);
	}

	private Annotation[] getCarriedAnnotations(Method method, List<InterceptorAnnotationWrapper> interceptors)
	{
		Set<Annotation> annotations = Arrays.stream(method.getAnnotations())
				.collect(Collectors.toSet());
		interceptors.stream().map(InterceptorAnnotationWrapper::getAnnotation).forEach(annotations::add);
		return annotations.toArray(new Annotation[annotations.size()]);
	}

	private BytebuddyInterceptor getPipeline(List<InterceptorAnnotationWrapper> interceptors)
	{
		List<Interceptor> interceptorPipeline = interceptors
				.stream()
				.map(InterceptorAnnotationWrapper::getInterceptor)
				.collect(Collectors.toList());
		return BytebuddyInterceptor.newInstance(interceptorPipeline);
	}

	private static final class InterceptorAnnotationWrapper extends Bean
	{
		private final Interceptor interceptor;
		private final Annotation annotation;

		InterceptorAnnotationWrapper(Interceptor interceptor, Annotation annotation)
		{
			this.interceptor = interceptor;
			this.annotation = annotation;
		}

		public Interceptor getInterceptor()
		{
			return this.interceptor;
		}

		public Annotation getAnnotation()
		{
			return this.annotation;
		}
	}

}