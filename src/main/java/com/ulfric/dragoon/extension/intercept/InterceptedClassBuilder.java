package com.ulfric.dragoon.extension.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.Parameters;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.reflect.Methods;
import com.ulfric.dragoon.stereotype.Stereotypes;

import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class InterceptedClassBuilder<T> {

	private final ObjectFactory factory;
	private final Class<T> parent;
	private DynamicType.Builder<T> builder;
	private boolean changed;

	public InterceptedClassBuilder(ObjectFactory factory, Class<T> parent) {
		this.factory = factory;
		this.parent = parent;
		this.builder = Classes.extend(parent);
	}

	public Class<? extends T> build() {
		this.run();

		if (!this.changed) {
			return this.parent;
		}

		return this.builder.make().load(this.parent.getClassLoader()).getLoaded();
	}

	private void run() {
		for (Method method : Methods.getOverridableMethods(this.parent)) {
			List<Interceptor<?>> interceptors = Stereotypes.getStereotypes(method, Intercept.class)
					.stream()
					.map(intercept -> this.createInterceptor(method, intercept))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

			if (interceptors.isEmpty()) {
				continue;
			}

			InterceptorPipeline pipeline = new InterceptorPipeline(interceptors);

			this.builder = this.builder.method(ElementMatchers.is(method))
					.intercept(MethodDelegation.to(pipeline))
					.annotateMethod(method.getDeclaredAnnotations());

			this.changed = true;
		}
	}

	private Interceptor<?> createInterceptor(Executable call, Annotation annotation) {
		Parameters parameters = Parameters.unqualified(call, annotation);
		return (Interceptor<?>) this.factory.requestUnspecific(annotation.annotationType(), parameters);
	}

}
