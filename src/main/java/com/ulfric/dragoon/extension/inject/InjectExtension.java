package com.ulfric.dragoon.extension.inject;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.creator.Creator;
import com.ulfric.dragoon.reflect.FieldProfile;
import com.ulfric.dragoon.value.Lazy;

public class InjectExtension extends Extension {

	@Creator
	private Factory factory;

	private final Lazy<FieldProfile> fields = Lazy.of(this::createFieldProfile);

	private FieldProfile createFieldProfile()
	{
		return FieldProfile.builder()
				.setFactory(this.factory)
				.setFlagToSearchFor(Inject.class)
				.build();
	}

	@Override
	public <T> T transform(T value)
	{
		this.fields.get().accept(value);
		return value;
	}

}