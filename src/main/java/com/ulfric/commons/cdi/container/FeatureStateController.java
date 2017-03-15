package com.ulfric.commons.cdi.container;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

final class FeatureStateController {

	private final Feature owner;
	private final Set<Feature> states = new LinkedHashSet<>();

	FeatureStateController(Feature owner)
	{
		this.owner = owner;
	}

	void install(Feature feature)
	{
		Objects.requireNonNull(feature);

		if (this.states.add(feature))
		{
			this.refreshFeature(feature);
		}
	}

	void refresh()
	{
		this.states.forEach(this::refreshFeature);
	}

	private void refreshFeature(Feature feature)
	{
		if (this.owner.isLoaded() && !feature.isLoaded())
		{
			feature.load();
		}

		if (this.owner.isEnabled() && !feature.isEnabled())
		{
			feature.enable();
		}
		else if (this.owner.isDisabled() && !feature.isDisabled())
		{
			feature.disable();
		}
	}

}