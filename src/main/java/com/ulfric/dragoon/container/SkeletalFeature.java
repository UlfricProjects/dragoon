package com.ulfric.dragoon.container;

import java.util.function.BooleanSupplier;

public class SkeletalFeature implements Feature {

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
		return Feature.super.isUnloaded();
	}

	@Override
	public final boolean isEnabled()
	{
		return this.enabled;
	}

	@Override
	public final boolean isDisabled()
	{
		return Feature.super.isDisabled();
	}

	@Override
	public final void load()
	{
		this.verify(this::isUnloaded);

		this.loaded = true;
		this.onStateChange();
		this.onLoad();
	}

	@Override
	public final void enable()
	{
		this.verify(this::isDisabled);

		this.loadIfNotLoaded();

		this.enabled = true;
		this.onStateChange();
		this.onEnable();
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

		this.enabled = false;
		this.onStateChange();
		this.onDisable();
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