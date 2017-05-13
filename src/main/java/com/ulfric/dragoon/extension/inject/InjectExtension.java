package com.ulfric.dragoon.extension.inject;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.creator.Creator;
import com.ulfric.dragoon.reflect.FieldProfile;
import com.ulfric.dragoon.stereotype.Stereotypes;
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
				.setFailureStrategy((type, field) ->
				{
					Inject inject = Stereotypes.getFirst(field, Inject.class);

					if (inject == null || !inject.optional())
					{
						throw new IllegalArgumentException("Failed to inject non-optional " + type + " into field " + field.getName());
					}
				})
				.build();
	}

	@Override
	public <T> T transform(T value)
	{
		this.fields.get().accept(value);
		return value;
	}

}