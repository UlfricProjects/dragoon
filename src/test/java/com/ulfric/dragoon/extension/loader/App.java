package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Container;

public class App extends Application {

	@Loader
	Container container;

	public App()
	{
		this.addStartHook(() ->
		{
			Apps.last = this.container;
		});
	}

}