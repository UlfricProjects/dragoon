package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.reflect.FieldProfile;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

public class LoaderExtension extends Extension {

	private static final Map<Class<?>, Boolean> INJECTIONS = new IdentityHashMap<>();

	static boolean isInjectionTarget(Class<?> type) {
		return LoaderExtension.INJECTIONS.computeIfAbsent(type, LoaderExtension::computeInjectionTarget);
	}

	private static boolean computeInjectionTarget(Class<?> type) {
		if (Classes.isRoot(type)) {
			return false;
		}

		if (type.isAnnotationPresent(Loader.class)) {
			return true;
		}

		for (Field field : type.getDeclaredFields()) {
			if (field.isAnnotationPresent(Loader.class)) {
				return true;
			}
		}

		return LoaderExtension.computeInjectionTarget(type.getSuperclass());
	}

	private final FieldProfile fields =
	        FieldProfile.builder().setFactory(LoaderFactory.INSTANCE).setFlagToSearchFor(Loader.class)
	                .setTypeResolverForMappingBindingsOfFieldTypes((object, field) -> object.getClass()).build();

	@Override
	public <T> Class<? extends T> transform(Class<T> type) {
		if (!LoaderExtension.isInjectionTarget(type)) {
			return type;
		}

		ClassLoader loader = type.getClassLoader();
		if (loader instanceof OwnedClassLoader) {
			if (!type.isAnnotationPresent(Loader.class)) {
				return type;
			}
		}

		OwnedClassLoader redefiner = new OwnedClassLoader(loader);
		return Classes.translate(type, redefiner);
	}

	@Override
	public <T> T transform(T value) {
		Class<?> type = value.getClass();
		if (LoaderExtension.isInjectionTarget(type)) // TODO it might not inject
		{
			if (type.isAnnotationPresent(Loader.class)) {
				this.injectLoader(value);
			}

			// TODO can be in else?
			this.fields.accept(value);
		}
		return value;
	}

	private void injectLoader(Object value) {
		ClassLoader loader = value.getClass().getClassLoader();
		if (loader instanceof OwnedClassLoader) {
			OwnedClassLoader owner = (OwnedClassLoader) loader;
			if (owner.getOwner() == null) {
				owner.setOwner(value);
			}
		}
	}

}
