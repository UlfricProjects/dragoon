package com.ulfric.commons.cdi.container;

import java.util.function.BooleanSupplier;

public class SkeletalComponent implements Component {

	protected boolean loaded;
	protected boolean enabled;

	@Override
	public final boolean isLoaded()
	{
		return this.loaded;
	}

	@Override
	public final boolean isUnloaded()
	{
		return Component.super.isUnloaded();
	}

	@Override
	public final boolean isEnabled()
	{
		return this.enabled;
	}

	@Override
	public final boolean isDisabled()
	{
		return Component.super.isDisabled();
	}

	@Override
	public final void load()
	{
		this.verify(this::isUnloaded);

		this.onLoad();
		this.loaded = true;
		this.onStateChange();
	}

	@Override
	public final void enable()
	{
		this.verify(this::isDisabled);

		this.loadIfNotLoaded();

		this.onEnable();
		this.enabled = true;
		this.onStateChange();
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

		this.onDisable();
		this.enabled = false;
		this.onStateChange();
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

	public void onStateChange()
	{

	}

	public void onLoad()
	{

	}

	public void onEnable()
	{

	}

	public void onDisable()
	{

	}

}