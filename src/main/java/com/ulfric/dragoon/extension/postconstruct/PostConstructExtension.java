package com.ulfric.dragoon.extension.postconstruct;

import com.ulfric.dragoon.exception.Try;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.stereotype.Stereotypes;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class PostConstructExtension extends Extension {

	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	@Override
	public <T> T transform(T value) {
		Class<?> lastRealCode = Classes.getNonDynamic(value.getClass());

		for (Method method : lastRealCode.getDeclaredMethods()) {
			if (!Stereotypes.isPresent(method, PostConstruct.class)) {
				continue;
			}

			method.setAccessible(true);
			Object owner = Modifier.isStatic(method.getModifiers()) ? null : value;
			Try.toRun(() -> method.invoke(owner, EMPTY_OBJECT_ARRAY));
		}

		return value;
	}

	@Override
	public int getPriority() {
		return Extension.LOW_PRIORITY;
	}

}
