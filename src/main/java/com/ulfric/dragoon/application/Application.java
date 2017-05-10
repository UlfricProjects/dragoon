package com.ulfric.dragoon.application;

public class Application {

	private boolean running;

	public final boolean isRunning()
	{
		return this.running;
	}

	public final void start()
	{
		this.running = true;
		this.handleStart();
	}

	public final void shutdown()
	{
		this.running = false;
		this.handleShutdown();
	}

	protected void handleStart() { }

	protected void handleShutdown() { }

}