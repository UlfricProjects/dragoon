package com.ulfric.dragoon.extension.intercept;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.creator.Creator;

public class InterceptExtension extends Extension {

	@Creator
	private Factory factory;

	@Override
	public <T> Class<T> transform(Class<T> type)
	{
		for (Method method : type.getMethods())
		{
			if (!this.isOverridable(method))
			{
				continue;
			}

			
		}
		return type;
	}

	private boolean isOverridable(Method method)
	{
		int modifier = method.getModifiers();
		return !Modifier.isFinal(modifier) && !Modifier.isStatic(modifier);
	}

}