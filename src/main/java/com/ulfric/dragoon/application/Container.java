package com.ulfric.dragoon.application;

import com.ulfric.dragoon.extension.Extensible;

public class Container implements Extensible<Class<? extends Application>>, Application {

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
	public void install(Class<? extends Application> extension)
	{
		// TODO Auto-generated method stub
	}

}