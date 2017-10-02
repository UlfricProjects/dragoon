package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Container;
import com.ulfric.dragoon.extension.inject.Inject;

public class App extends Application {

	@Inject
	Container container;

	public App() {
		this.addBootHook(() -> Apps.last = this.container);
	}

}
