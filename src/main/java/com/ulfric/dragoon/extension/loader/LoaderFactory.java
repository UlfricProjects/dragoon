package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.Factory;

public enum LoaderFactory implements Factory {

	INSTANCE;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T request(Class<T> type)
	{
		ClassLoader loader = type.getClassLoader();
		if (loader instanceof OwnedClassLoader)
		{
			return (T) ((OwnedClassLoader) loader).getOwner();
		}
		return null;
	}

}