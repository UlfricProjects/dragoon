package com.ulfric.dragoon.extension.intercept;

import java.lang.reflect.Modifier;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.inject.Inject;

public class InterceptExtension extends Extension {

	@Inject
	private ObjectFactory factory;

	@Override
	public <T> Class<? extends T> transform(Class<T> type) {
		if (!isExtensible(type)) {
			return type;
		}

		return new InterceptedClassBuilder<>(this.factory, type).build();
	}

	private boolean isExtensible(Class<?> type) {
		if (type.isEnum() || type.isArray() || type.isPrimitive()) {
			return false;
		}

		return !type.isEnum() &&
				!type.isArray() &&
				!type.isPrimitive() &&
				!Modifier.isFinal(type.getModifiers());
	}

}
