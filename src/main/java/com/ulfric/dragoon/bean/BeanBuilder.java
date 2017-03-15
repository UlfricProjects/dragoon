package com.ulfric.dragoon.bean;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ulfric.commons.bean.Bean;

import net.bytebuddy.ByteBuddy;
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

	T build()
	{
		@SuppressWarnings("unchecked")
		T bean = (T) this.builder.make().load(this.interfaceType.getClassLoader()).getLoaded();

		return bean;
	}

	private DynamicType.Builder<Bean> createBuilder()
	{
		return new ByteBuddy()
				.subclass(Bean.class)
				.implement(this.interfaceType)
				.method(ElementMatchers.isGetter().or(ElementMatchers.isSetter()))
				.intercept(FieldAccessor.ofBeanProperty());
	}

	private void makeFields()
	{
		this.findGetters().forEach(this::createFieldFromGetter);
	}

	private List<Getter> findGetters()
	{
		return this.wrapMethods(
				Stream.of(this.interfaceType.getMethods())
						.filter(BeanBuilder.GETTER_PREDICATE)
						.collect(Collectors.toList())
		);
	}

	private void createFieldFromGetter(Getter getter)
	{
		this.builder = this.builder.defineField(getter.getFieldName(), getter.getType(), Visibility.PRIVATE);
	}

	private List<Getter> wrapMethods(List<Method> methods)
	{
		return methods
				.stream()
				.map(BeanBuilder.GETTER_MAPPER)
				.collect(Collectors.toList());
	}

}
