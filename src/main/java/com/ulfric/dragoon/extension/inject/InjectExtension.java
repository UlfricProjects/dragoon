package com.ulfric.dragoon.extension.inject;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.Parameters;
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
		        .setFailureStrategy((type, parameters) -> {
			        Inject inject = Stereotypes.getFirst(parameters.getQualifier(), Inject.class);

			        if (inject == null || !inject.optional()) {
				        throw new IllegalStateException(
				                "Failed to inject non-optional " + type + " into " +
				                		Parameters.getQualifiedName(parameters));
			        }
		        }).build();
	}

	@Override
	public <T> T transform(T value) {
		fields.get().accept(value);
		return value;
	}

}
