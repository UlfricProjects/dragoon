package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.creator.Creator;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.reflect.FieldProfile;
import com.ulfric.dragoon.value.Lazy;

public class LoaderExtension extends Extension {

	@Creator
	private Factory factory;

	private final Lazy<FieldProfile> fields = Lazy.of(this::createFieldProfile);

	private FieldProfile createFieldProfile()
	{
		return FieldProfile.builder()
				.setFactory(new LoaderFactory(this.factory))
				.setFlagToSearchFor(Loader.class)
				.build();
	}

	@Override
	public <T> Class<? extends T> transform(Class<T> type)
	{
		if (!type.isAnnotationPresent(Loader.class))
		{
			return type;
		}

		OwnedClassLoader redefiner = new OwnedClassLoader(type.getClassLoader());
		return Classes.extend(type).make().load(redefiner).getLoaded(); // TODO redefine(type) instead?
	}

	@Override
	public <T> T transform(T value)
	{
		Class<?> type = value.getClass();
		if (type.isAnnotationPresent(Loader.class))
		{
			this.injectLoader(value);
		}
		this.fields.get().accept(value);
		return value;
	}

	private void injectLoader(Object value)
	{
		ClassLoader loader = value.getClass().getClassLoader();
		if (loader instanceof OwnedClassLoader)
		{
			OwnedClassLoader owner = (OwnedClassLoader) loader;
			if (owner.getOwner() == null)
			{
				owner.setOwner(value);
			}
		}
	}

}