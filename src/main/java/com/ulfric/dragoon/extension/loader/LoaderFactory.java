package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.Parameters;

public enum LoaderFactory implements Factory {

	INSTANCE;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T request(Class<T> type) {
		ClassLoader loader = type.getClassLoader();
		if (loader instanceof OwnedClassLoader) {
			return (T) ((OwnedClassLoader) loader).getOwner();
		}
		return null;
	}

	@Override
	public <T> T request(Class<T> type, Parameters parameters) {
		return request(type);
	}

}
