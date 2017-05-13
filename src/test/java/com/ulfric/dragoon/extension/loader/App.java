package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Container;

public class App extends Application {

	static App last;

	@Loader
	Container container;

	public App()
	{
		//System.out.println(this.getClass().getClassLoader());
		App.last = this;
	}

}