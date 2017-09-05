package com.ulfric.dragoon.extension.inject;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.reflect.FieldProfile;
import com.ulfric.dragoon.stereotype.Stereotypes;
import com.ulfric.dragoon.value.Lazy;

import java.util.Objects;

public class InjectExtension extends Extension {

	private final Factory factory;
	private final Lazy<FieldProfile> fields = Lazy.of(this::createFieldProfile);

	public InjectExtension(Factory factory) {
		Objects.requireNonNull(factory, "factory");
		this.factory = factory;
	}

	private FieldProfile createFieldProfile() {
		return FieldProfile.builder()
				.setFactory(this.factory)
				.setFlagToSearchFor(Inject.class)
		        .setFailureStrategy((type, field) -> {
			        Inject inject = Stereotypes.getFirst(field, Inject.class);

			        if (inject == null || !inject.optional()) {
				        throw new IllegalStateException(
				                "Failed to inject non-optional " + type + " into field " +
				                		FieldProfile.getFieldName(field));
			        }
		        }).build();
	}

	@Override
	public <T> T transform(T value) {
		fields.get().accept(value);
		return value;
	}

}
