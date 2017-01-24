package com.ulfric.commons.cdi.container;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

final class ComponentStateController {

	private final Component owner;
	private final Set<Component> states = new LinkedHashSet<>();

	ComponentStateController(Component owner)
	{
		this.owner = owner;
	}

	void install(Component component)
	{
		Objects.requireNonNull(component);

		if (this.states.add(component))
		{
			this.refreshComponent(component);
		}
	}

	void refresh()
	{
		this.states.forEach(this::refreshComponent);
	}

	private void refreshComponent(Component component)
	{
		if (this.owner.isLoaded() && !component.isLoaded())
		{
			component.load();
		}

		if (this.owner.isEnabled() && !component.isEnabled())
		{
			component.enable();
		}
		else if (this.owner.isDisabled() && !component.isDisabled())
		{
			component.disable();
		}
	}

}