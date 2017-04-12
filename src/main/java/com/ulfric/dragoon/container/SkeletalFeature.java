package com.ulfric.dragoon.container;

import java.util.function.BooleanSupplier;

public class SkeletalFeature implements Feature {

	private boolean enabled;

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
	public final void enable()
	{
		this.verify(this::isDisabled);

		this.enabled = true;
		this.onStateChange();
		this.onEnable();
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

	public void onEnable()
	{

	}

	public void onDisable()
	{

	}

}