package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.application.OwnedClassLoader;
import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.reflect.Classes;

public class LoaderExtension extends Extension {

	@Override
	public <T> Class<? extends T> transform(Class<T> type) {
		if (Extensible.class.isAssignableFrom(type)) {
			return Classes.translate(type, new OwnedClassLoader(type.getClassLoader()));
		}
		return type;
	}

	@Override
	public <T> T transform(T value) {
		ClassLoader loader = value.getClass().getClassLoader();

		if (loader instanceof OwnedClassLoader) {
			OwnedClassLoader ownedloader = (OwnedClassLoader) loader;
			if (ownedloader.getOwner() == null) {
				ownedloader.setOwner(value);
			}
		}

		return value;
	}

}
