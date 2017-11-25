package com.ulfric.dragoon.reflect;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.Parameters;
import com.ulfric.dragoon.exception.Try;
import com.ulfric.dragoon.qualifier.FieldQualifier;
import com.ulfric.dragoon.qualifier.Qualifier;
import com.ulfric.dragoon.stereotype.Stereotypes;

public final class FieldProfile implements Consumer<Object> {

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Factory factory;
		private Class<? extends Annotation> flag;
		private Function<Parameters, Class<?>> typeResolver;
		private BiConsumer<Class<?>, Parameters> failureStrategy;

		Builder() {}

		public FieldProfile build() {
			Objects.requireNonNull(factory, "factory");
			Objects.requireNonNull(flag, "flag");

			Function<Parameters, Class<?>> typeResolver = this.typeResolver;
			if (typeResolver == null) {
				typeResolver = (parameters) -> Classes.getRawType(parameters.getQualifier().getType());
			}

			BiConsumer<Class<?>, Parameters> failureStrategy = this.failureStrategy;
			if (failureStrategy == null) {
				failureStrategy = (type, parameters) -> {
					throw new IllegalArgumentException("Failed to inject " + type + " into field " +
							Parameters.getQualifiedName(parameters));
				};
			}

			return new FieldProfile(factory, flag, typeResolver, failureStrategy);
		}

		public Builder setFactory(Factory factory) {
			this.factory = factory;
			return this;
		}

		public Builder setFlagToSearchFor(Class<? extends Annotation> flag) {
			this.flag = flag;
			return this;
		}

		public Builder setTypeResolverForMappingBindingsOfFieldTypes(Function<Parameters, Class<?>> typeResolver) {
			this.typeResolver = typeResolver;
			return this;
		}

		public Builder setFailureStrategy(BiConsumer<Class<?>, Parameters> failureStrategy) {
			this.failureStrategy = failureStrategy;
			return this;
		}
	}

	private final Factory factory;
	private final Class<? extends Annotation> flag;
	private final Function<Parameters, Class<?>> typeResolver;
	private final BiConsumer<Class<?>, Parameters> failureStrategy;
	private final Map<Class<?>, List<Setter>> requests = new IdentityHashMap<>();

	private FieldProfile(Factory factory, Class<? extends Annotation> flag,
	        Function<Parameters, Class<?>> typeResolver, BiConsumer<Class<?>, Parameters> failureStrategy) {
		this.factory = factory;
		this.flag = flag;
		this.typeResolver = typeResolver;
		this.failureStrategy = failureStrategy;
	}

	@Override
	public void accept(Object setValues) {
		for (Setter handle : getSetters(setValues.getClass())) {

			Parameters parameters = Parameters.qualifiedHolder(handle.qualifier, setValues);
			Class<?> injectType = this.typeResolver.apply(parameters);
			Object value = factory.request(injectType, parameters);

			if (value != null) {
				Try.toRun(() -> {
					handle.setter.invokeExact(setValues, value);
				});
			} else {
				this.failureStrategy.accept(injectType, parameters);
			}
		}
	}

	public boolean containsFields(Class<?> type) {
		return !getSetters(type).isEmpty();
	}

	public List<Setter> getSetters(Class<?> type) {
		return requests.computeIfAbsent(type, this::createSetters);
	}

	private List<Setter> createSetters(Class<?> type) {
		return Stereotypes.getAnnotatedInstanceFields(type, this.flag).stream().map(field -> {
			Qualifier qualifier = new FieldQualifier(field);
			MethodHandle setter = Handles.setter(field);
			return new Setter(qualifier, setter);
		}).collect(Collectors.toList());
	}

	public static final class Setter {
		final Qualifier qualifier;
		final MethodHandle setter;

		Setter(Qualifier qualifier, MethodHandle setter) {
			this.qualifier = qualifier;
			this.setter = setter;
		}

		public Qualifier getQualifier() {
			return this.qualifier;
		}
	}

}
