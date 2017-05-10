package com.ulfric.dragoon.application;

import com.ulfric.dragoon.extension.Result;

public enum InstallApplicationResult implements Result {

	SUCCESS(true),
	ALREADY_INSTALLED(false),
	SELF_INSTALLATION(false);

	private final boolean success;

	private InstallApplicationResult(boolean success)
	{
		this.success = success;
	}

	@Override
	public boolean isSuccess()
	{
		return this.success;
	}

}