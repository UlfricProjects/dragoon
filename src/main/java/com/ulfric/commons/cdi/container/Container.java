package com.ulfric.commons.cdi.container;

import java.util.function.BooleanSupplier;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;

public class Container implements Component {

	private final ComponentStateController components = new ComponentStateController(this);
	private boolean loaded;
	private boolean enabled;

	@Inject
	private ObjectFactory factory;

	@Override
	public final boolean isLoaded()
	{
		return this.loaded;
	}

	@Override
	public final boolean isEnabled()
	{
		return this.enabled;
	}

	@Override
	public final void load()
	{
		this.verify(this::isUnloaded);

		this.load();
		this.loaded = true;
		this.notifyComponents();
	}

	@Override
	public final void enable()
	{
		this.verify(this::isDisabled);

		this.loadIfNotLoaded();

		this.enable();
		this.enabled = true;
		this.notifyComponents();
	}

	private void loadIfNotLoaded()
	{
		if (!this.isLoaded())
		{
			this.load();
		}
	}

	@Override
	public final void disable()
	{
		this.verify(this::isEnabled);

		this.disable();
		this.enabled = false;
		this.notifyComponents();
	}

	private void verify(BooleanSupplier flag)
	{
		if (flag.getAsBoolean())
		{
			return;
		}

		throw new IllegalStateException("Failed to verify state: " + this.getVerifyCallerMethodName());
	}

	private String getVerifyCallerMethodName()
	{
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}

	private void notifyComponents()
	{
		this.components.refresh();
	}

}