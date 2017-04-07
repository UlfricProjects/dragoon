package com.ulfric.dragoon.bean;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.apache.commons.lang3.ClassUtils;

import com.ulfric.commons.bean.Bean;
import com.ulfric.commons.exception.Try;
import com.ulfric.dragoon.Dynamic;
import com.ulfric.dragoon.bean.FieldInfoExtractor.FieldInfo;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

final class BeanBuilder<T> {

	private final Class<T> interfaceType;
	private DynamicType.Builder<Bean> builder;

	BeanBuilder(Class<T> interfaceType)
	{
		this.interfaceType = interfaceType;
		this.builder = this.createBuilder();
	}

	Class<? extends T> build()
	{
		this.make();

		@SuppressWarnings("unchecked")
		Class<? extends T> beanClass = (Class<? extends T>) this.builder.make().load(this.getClass().getClassLoader()).getLoaded();

		return beanClass;
	}

	private void make()
	{
		this.implementMethods();
		this.createFields();
		this.restoreAnnotationsFromParent();
	}

	private DynamicType.Builder<Bean> createBuilder()
	{
		return new ByteBuddy(ClassFileVersion.JAVA_V8)
				.subclass(Bean.class)
				.implement(this.interfaceType)
				.implement(Dynamic.class);
	}

	private void implementMethods()
	{
		this.streamMethods(ElementMatchers.isGetter().or(ElementMatchers.isSetter()))
				.map(this::unwrapDescription)
				.forEach(this::implementMethod);
	}

	private Stream<MethodDescription.InDefinedShape> streamMethods(ElementMatcher.Junction<? super MethodDescription.InDefinedShape> matcher)
	{
		return new TypeDescription.ForLoadedType(this.interfaceType)
				.getDeclaredMethods()
				.filter(matcher)
				.stream();
	}

	private Method unwrapDescription(MethodDescription description)
	{
		Class<?>[] parameters = this.unwrapParameters(description);

		return Try.to(() -> this.interfaceType.getDeclaredMethod(description.getInternalName(), parameters));
	}

	private Class<?>[] unwrapParameters(MethodDescription description)
	{
		return description.getParameters()
					.stream()
					.map(this::unwrapParameter)
					.toArray(Class<?>[]::new);
	}

	private Class<?> unwrapParameter(ParameterDescription description)
	{
		return Try.to(() -> ClassUtils.getClass(description.getType().getTypeName()));
	}

	private void implementMethod(Method method)
	{
		this.builder =
				this.builder.method(ElementMatchers.is(method))
						.intercept(FieldAccessor.ofBeanProperty())
						.annotateMethod(method.getDeclaredAnnotations());
	}

	private void createFields()
	{
		this.streamMethods(ElementMatchers.isGetter())
				.map(this::unwrapDescription)
				.forEach(this::createField);
	}

	private void createField(Method method)
	{
		FieldInfo info = FieldInfoExtractor.from(method);

		this.builder = this.builder
						.defineField(info.getFieldName(), info.getFieldType(), Visibility.PRIVATE)
						.annotateField(info.getAnnotations());
	}

	private void restoreAnnotationsFromParent()
	{
		this.builder = this.builder.annotateType(this.interfaceType.getDeclaredAnnotations());
	}

}
