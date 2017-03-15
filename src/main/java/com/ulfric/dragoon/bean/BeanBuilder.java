package com.ulfric.dragoon.bean;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.ulfric.commons.bean.Bean;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.matcher.ElementMatchers;

final class BeanBuilder<T> {

	private static final Predicate<Method>  GETTER_PREDICATE = new GetterPredicate();
	private static final GetterMapper GETTER_MAPPER = new GetterMapper();

	private final Class<T> interfaceType;
	private DynamicType.Builder<Bean> builder;

	BeanBuilder(Class<T> interfaceType)
	{
		this.interfaceType = interfaceType;
		this.builder = this.createBuilder();
		this.makeFields();
	}

	Class<? extends T> build()
	{
		@SuppressWarnings("unchecked")
		Class<? extends T> beanClass = (Class<? extends T>) this.builder.make().load(this.getClass().getClassLoader()).getLoaded();

		return beanClass;
	}

	private DynamicType.Builder<Bean> createBuilder()
	{
		return new ByteBuddy(ClassFileVersion.JAVA_V8)
				.subclass(Bean.class)
				.implement(this.interfaceType)
				.method(ElementMatchers.isGetter().or(ElementMatchers.isSetter()))
				.intercept(FieldAccessor.ofBeanProperty());
	}

	private void makeFields()
	{
		this.findGetters().forEach(this::addFieldFromGetter);
	}

	private Stream<Getter> findGetters()
	{
		return Stream.of(this.interfaceType.getMethods())
						.filter(BeanBuilder.GETTER_PREDICATE)
						.map(BeanBuilder.GETTER_MAPPER);
	}

	private void addFieldFromGetter(Getter getter)
	{
		this.builder = this.builder.defineField(getter.getFieldName(), getter.getType(), Visibility.PRIVATE);
	}

}
