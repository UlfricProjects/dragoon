package com.ulfric.commons.cdi.container;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

final class ComponentStateController {

	private final Component owner;
	private final Set<Component> states = new LinkedHashSet<>();

	public ComponentStateController(Component owner)
	{
		this.owner = owner;
	}

	public void install(Component state)
	{
		Objects.requireNonNull(state);

		if (this.states.add(state))
		{
			this.refreshState(state);
		}
	}

	public void refresh()
	{
		this.states.forEach(this::refreshState);
	}

	private void refreshState(Component state)
	{
		if (this.owner.isLoaded() && !state.isLoaded())
		{
			state.load();
		}

		if (this.owner.isEnabled() && !state.isEnabled())
		{
			state.enable();
		}
		else if (this.owner.isDisabled() && !state.isDisabled())
		{
			state.disable();
		}
	}

}