package com.ulfric.dragoon.extension.intercept;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.creator.Creator;

public class InterceptExtension extends Extension {

	@Creator
	private ObjectFactory factory;

	@Override
	public <T> Class<? extends T> transform(Class<T> type)
	{
		return new InterceptedClassBuilder<>(this.factory, type).build();
	}

}