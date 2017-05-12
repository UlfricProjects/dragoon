package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.Factory;

public class LoaderFactory implements Factory {

	private final Factory delegate;

	public LoaderFactory(Factory delegate)
	{
		this.delegate = delegate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T request(Class<T> type)
	{
		ClassLoader loader = type.getClassLoader();
		if (loader instanceof OwnedClassLoader)
		{
			return (T) ((OwnedClassLoader) loader).getOwner();
		}
		return this.delegate.request(type);
	}

}