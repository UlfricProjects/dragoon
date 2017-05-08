package com.ulfric.dragoon.container;

import com.ulfric.dragoon.extension.Extensible;

public class Container implements Extensible<Class<? extends Content>>, Content {

	private boolean running;

	public boolean isRunning()
	{
		return this.running;
	}

	public void run()
	{
		this.running = true;
	}

	public void stop()
	{
		this.running = false;
	}

	@Override
	public void install(Class<? extends Content> extension)
	{
		// TODO Auto-generated method stub
	}

}