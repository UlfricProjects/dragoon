package com.ulfric.dragoon.reflect;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.exception.Try;
import com.ulfric.dragoon.function.TriFunction;
import com.ulfric.dragoon.stereotype.Stereotypes;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class FieldProfile implements Consumer<Object> {

	public static String getFieldName(Field field) {
		return Classes.getNonDynamic(field.getDeclaringClass()).getName() + ':' + field.getName();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Factory factory;
		private Class<? extends Annotation> flag;
		private Predicate<GetterAndSetter> filter;
		private BiFunction<Object, Field, Class<?>> typeResolver;
		private BiConsumer<Class<?>, Field> failureStrategy;
		private boolean sendFieldToFactory;

		Builder() {}

		public FieldProfile build() {
			Objects.requireNonNull(factory, "factory");
			Objects.requireNonNull(flag, "flag");

			Predicate<GetterAndSetter> filter = this.filter;
			if (filter == null) {
				filter = ignore -> true;
			}

			BiFunction<Object, Field, Class<?>> typeResolver = this.typeResolver;
			if (typeResolver == null) {
				typeResolver = (ignore, field) -> field.getType();
			}

			BiConsumer<Class<?>, Field> failureStrategy = this.failureStrategy;
			if (failureStrategy == null) {
				failureStrategy = (type, field) -> {
					throw new IllegalArgumentException("Failed to inject " + type + " into field " + field.getName());
				};
			}

			return new FieldProfile(factory, flag, filter, typeResolver, failureStrategy, sendFieldToFactory);
		}

		public Builder setFactory(Factory factory) {
			this.factory = factory;
			return this;
		}

		public Builder setFlagToSearchFor(Class<? extends Annotation> flag) {
			this.flag = flag;
			return this;
		}

		public Builder setFilterForIgnoringFieldsEachInvocation(Predicate<GetterAndSetter> filter) {
			this.filter = filter;
			return this;
		}

		public Builder setTypeResolverForMappingBindingsOfFieldTypes(BiFunction<Object, Field, Class<?>> typeResolver) {
			this.typeResolver = typeResolver;
			return this;
		}

		public Builder setFailureStrategy(BiConsumer<Class<?>, Field> failureStrategy) {
			this.failureStrategy = failureStrategy;
			return this;
		}

		public Builder setSendFieldToFactory(boolean sendFieldToFactory) {
			this.sendFieldToFactory = sendFieldToFactory;
			return this;
		}
	}

	private final Class<? extends Annotation> flag;
	private final Predicate<GetterAndSetter> filter;
	private final BiFunction<Object, Field, Class<?>> typeResolver;
	private final TriFunction<Class<?>, Object, Field, Object> instanceCreator;
	private final BiConsumer<Class<?>, Field> failureStrategy;
	private final Map<Class<?>, List<GetterAndSetter>> requests = new IdentityHashMap<>();

	private FieldProfile(Factory factory, Class<? extends Annotation> flag, Predicate<GetterAndSetter> filter,
	        BiFunction<Object, Field, Class<?>> typeResolver, BiConsumer<Class<?>, Field> failureStrategy,
	        boolean sendFieldToFactory) {
		this.flag = flag;
		this.filter = filter;
		this.typeResolver = typeResolver;
		this.failureStrategy = failureStrategy;

		this.instanceCreator = sendFieldToFactory ? (type, owner, field) -> factory.request(type, owner, field) :
			(type, owner, field) -> factory.request(type, owner);
	}

	@Override
	public void accept(Object setValues) {
		for (GetterAndSetter handle : this.requests.computeIfAbsent(setValues.getClass(),
		        this::createGettersAndSetters)) {
			if (!this.filter.test(handle)) {
				continue;
			}

			Class<?> injectType = this.typeResolver.apply(setValues, handle.field);
			Object value = instanceCreator.apply(injectType, setValues, handle.field);

			if (value != null) {
				Try.toRun(() -> {
					handle.setter.invokeExact(setValues, value);
				});
			} else {
				this.failureStrategy.accept(injectType, handle.field);
			}
		}
	}

	private List<GetterAndSetter> createGettersAndSetters(Class<?> type) {
		return Stereotypes.getAnnotatedInstanceFields(type, this.flag).stream().map(field -> {
			MethodHandle setter = Handles.setter(field);
			return new GetterAndSetter(field, setter);
		}).collect(Collectors.toList());
	}

	public static final class GetterAndSetter {
		final Field field;
		final MethodHandle setter;

		GetterAndSetter(Field field, MethodHandle setter) {
			this.field = field;
			this.setter = setter;
		}

		public Field getField() {
			// TODO clone?
			return this.field;
		}
	}

}
