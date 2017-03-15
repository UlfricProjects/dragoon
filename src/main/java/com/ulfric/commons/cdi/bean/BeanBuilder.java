package com.ulfric.commons.cdi.bean;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;
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

	private final Class<T> clazz;
	private DynamicType.Builder<Bean> builder;

	BeanBuilder(Class<T> clazz)
	{
		this.clazz = clazz;
		this.builder = this.createBuilder();
		this.makeFields();
	}

	T build()
	{
		@SuppressWarnings("unchecked")
		T bean = (T) this.builder.make().load(this.clazz.getClassLoader()).getLoaded();

		return bean;
	}

	private DynamicType.Builder<Bean> createBuilder()
	{
		return new ByteBuddy()
				.subclass(Bean.class)
				.implement(this.clazz)
				.method(ElementMatchers.isGetter().or(ElementMatchers.isSetter()))
				.intercept(FieldAccessor.ofBeanProperty());
	}

	private void makeFields()
	{
		this.findGetters().forEach(this::createFieldFromGetter);
	}

	private List<Getter> findGetters()
	{
		return this.wrapMethods(this.getMethods(BeanBuilder.GETTER_PREDICATE));
	}

	private void createFieldFromGetter(Getter getter)
	{
		this.builder = this.builder.defineField(getter.fieldName, getter.type, Visibility.PRIVATE);
	}

	private List<Method> getMethods(Predicate<Method> predicate)
	{
		return Stream.of(this.clazz.getMethods())
				.filter(predicate)
				.collect(Collectors.toList());
	}

	private List<Getter> wrapMethods(List<Method> methods)
	{
		return methods
				.stream()
				.map(BeanBuilder.GETTER_MAPPER)
				.collect(Collectors.toList());
	}

	private static final class GetterPredicate implements Predicate<Method>
	{

		private static final String METHOD_PREFIX = "get";
		private static final int NEXT_CHAR_LOCATION = 3;
		private static final Class<?> NOT_RETURN_TYPE = Void.TYPE;
		private static final int PARAMETER_COUNT = 0;

		@Override
		public boolean test(Method method)
		{
			String name = method.getName().toLowerCase();

			return name.startsWith(GetterPredicate.METHOD_PREFIX) &&
					Character.isUpperCase(method.getName().charAt(GetterPredicate.NEXT_CHAR_LOCATION)) &&
					!method.getReturnType().equals(GetterPredicate.NOT_RETURN_TYPE) &&
					method.getParameterCount() == GetterPredicate.PARAMETER_COUNT;
		}

	}

	private static final class GetterMapper implements Function<Method, Getter>
	{
		private static final int NEXT_CHAR_LOCATION = 3;

		@Override
		public Getter apply(Method method)
		{
			String name = this.lowerCaseFirstChar(
					method.getName().substring(GetterMapper.NEXT_CHAR_LOCATION)
			);

			return new Getter(name, method.getReturnType());
		}

		private String lowerCaseFirstChar(String string)
		{
			String firstLetter = string.substring(0, 1);

			return firstLetter.toLowerCase() + string.substring(1);
		}

	}

	private static final class Getter
	{
		private final Class<?> type;
		private final String fieldName;

		private Getter(String fieldName, Class<?> type)
		{
			this.fieldName = fieldName;
			this.type = type;
		}

	}

}
