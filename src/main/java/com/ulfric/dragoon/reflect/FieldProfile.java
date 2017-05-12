package com.ulfric.dragoon.reflect;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.exception.Try;
import com.ulfric.dragoon.stereotype.Stereotypes;

public final class FieldProfile implements Consumer<Object> {

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder
	{
		private Factory factory;
		private Class<? extends Annotation> flag;
		private Predicate<GetterAndSetter> filter;

		Builder() { }

		public FieldProfile build()
		{
			Objects.requireNonNull(this.factory, "factory");
			Objects.requireNonNull(this.flag, "flag");

			Predicate<GetterAndSetter> filter = this.filter;
			if (filter == null)
			{
				filter = ignore -> true;
			}

			return new FieldProfile(this.factory, this.flag, filter);
		}

		public Builder setFactory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public Builder setFlagToSearchFor(Class<? extends Annotation> flag)
		{
			this.flag = flag;
			return this;
		}

		public Builder setFilterForIgnoringFieldsEachInvocation(Predicate<GetterAndSetter> filter)
		{
			this.filter = filter;
			return this;
		}
	}

	private final Factory factory;
	private final Class<? extends Annotation> flag;
	private final Predicate<GetterAndSetter> filter;
	private final Map<Class<?>, List<GetterAndSetter>> requests = new IdentityHashMap<>();

	private FieldProfile(Factory factory, Class<? extends Annotation> flag, Predicate<GetterAndSetter> filter)
	{
		this.factory = factory;
		this.flag = flag;
		this.filter = filter;
	}

	@Override
	public void accept(Object setValues)
	{
		for (GetterAndSetter handle : this.requests.computeIfAbsent(setValues.getClass(), this::createGettersAndSetters))
		{
			if (!this.filter.test(handle))
			{
				continue;
			}

			Object value = this.factory.request(handle.type);

			if (value != null)
			{
				Try.to(() -> { handle.setter.invokeExact(setValues, value); });
			}
		}
	}

	private List<GetterAndSetter> createGettersAndSetters(Class<?> type)
	{
		return Stereotypes.getAnnotatedInstanceFields(type, this.flag)
				.stream()
				.map(field ->
				{
					MethodHandle setter = Handles.setter(field);
					return new GetterAndSetter(field, setter);
				})
				.collect(Collectors.toList());
	}

	public static final class GetterAndSetter
	{
		final Field field;
		final Class<?> type;
		final MethodHandle setter;

		GetterAndSetter(Field field, MethodHandle setter)
		{
			this.field = field;
			this.type = field.getType();
			this.setter = setter;
		}

		public Field getField()
		{
			// TODO clone?
			return this.field;
		}
	}

}