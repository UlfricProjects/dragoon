package com.ulfric.dragoon.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Application {

	private final List<StateHook> start = new ArrayList<>();
	private final List<StateHook> shutdown = new ArrayList<>();
	private boolean running;

	public final boolean isRunning()
	{
		return this.running;
	}

	public final void start()
	{
		if (this.isRunning())
		{
			return;
		}

		this.running = true;
		this.start.forEach(StateHook::run);
	}

	public final void shutdown()
	{
		if (!this.isRunning())
		{
			return;
		}

		this.running = false;
		this.shutdown.forEach(StateHook::run);
	}

	public final void addStartHook(StateHook hook)
	{
		Objects.requireNonNull(hook, "hook");
		this.start.add(hook);
	}

	public final void addShutdownHook(StateHook hook)
	{
		Objects.requireNonNull(hook, "hook");
		this.shutdown.add(hook);
	}

}