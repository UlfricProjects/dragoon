package com.ulfric.dragoon.extension.inject;

import java.lang.reflect.Modifier;
import java.util.Objects;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.Parameters;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.inject.internal.Injectable;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.reflect.FieldProfile;
import com.ulfric.dragoon.stereotype.Stereotypes;
import com.ulfric.dragoon.value.Lazy;

import net.bytebuddy.description.modifier.SyntheticState;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.matcher.ElementMatchers;

public class InjectExtension extends Extension {

	private final Factory factory;
	private final Lazy<FieldProfile> fields = Lazy.of(this::createFieldProfile);

	public InjectExtension(Factory factory) {
		Objects.requireNonNull(factory, "factory");
		this.factory = factory;
	}

	private FieldProfile createFieldProfile() {
		return FieldProfile.builder()
				.setFactory(this.factory)
				.setFlagToSearchFor(Inject.class)
		        .setFailureStrategy((type, parameters) -> {
			        Inject inject = Stereotypes.getFirst(parameters.getQualifier(), Inject.class);

			        if (inject == null || !inject.optional()) {
				        throw new IllegalStateException(
				                "Failed to inject non-optional " + type + " into " +
				                		Parameters.getQualifiedName(parameters));
			        }
		        }).build();
	}

	@Override
	public <T> Class<? extends T> transform(Class<T> type) {
		if (shouldPreventDoubleInjection(type)) {
			String field = "injectionRan";
			ClassLoader loader = type.getClassLoader() == null ? Injectable.class.getClassLoader() : type.getClassLoader();
			return Classes.extend(type)
				.implement(Injectable.class)
				.defineField(field, boolean.class, SyntheticState.SYNTHETIC, Visibility.PRIVATE)
				.method(ElementMatchers.isDeclaredBy(Injectable.class))
				.intercept(FieldAccessor.ofBeanProperty())
				.make()
				.load(loader)
				.getLoaded();
		}
		return type;
	}

	private boolean shouldPreventDoubleInjection(Class<?> type) {
		if (type.isAnnotation() || type.isInterface() || type.isPrimitive()) {
			return false;
		}

		int modifiers = type.getModifiers();
		return !Modifier.isFinal(modifiers) && !Modifier.isAbstract(modifiers);
	}

	@Override
	public <T> T transform(T value) {
		if (value instanceof Injectable) {
			Injectable injectable = (Injectable) value;

			if (injectable.getInjectionRan()) {
				return value;
			}

			injectable.setInjectionRan(true);
		}
		fields.get().accept(value);
		return value;
	}

}
