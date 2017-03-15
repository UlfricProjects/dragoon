package com.ulfric.dragoon.bean;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ClassUtils;

import com.ulfric.commons.bean.Bean;
import com.ulfric.commons.exception.Try;
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

	private final Set<FieldInfo> registeredFields = new HashSet<>();
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
		this.restoreAnnotationsFromParent();
	}

	private DynamicType.Builder<Bean> createBuilder()
	{
		return new ByteBuddy(ClassFileVersion.JAVA_V8)
				.subclass(Bean.class)
				.implement(this.interfaceType);
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
					.collect(Collectors.toList())
					.toArray(new Class[0]);
	}

	private Class<?> unwrapParameter(ParameterDescription description)
	{
		return Try.to(() -> ClassUtils.getClass(description.getType().getTypeName()));
	}

	private void implementMethod(Method method)
	{
		this.ensureFieldCreated(method);

		this.builder =
				this.builder.method(ElementMatchers.is(method))
						.intercept(FieldAccessor.ofBeanProperty())
						.annotateMethod(method.getDeclaredAnnotations());
	}

	private void ensureFieldCreated(Method method)
	{
		FieldInfo info = FieldInfoExtractor.from(method);

		if (!this.registeredFields.contains(info)) {
			this.createField(info);
			this.registeredFields.add(info);
		}
	}

	private void createField(FieldInfo info)
	{
		this.builder = this.builder.defineField(info.getFieldName(), info.getFieldType(), Visibility.PRIVATE);
	}

	private void restoreAnnotationsFromParent()
	{
		this.builder = this.builder.annotateType(this.interfaceType.getDeclaredAnnotations());
	}

}
