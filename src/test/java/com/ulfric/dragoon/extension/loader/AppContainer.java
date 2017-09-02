package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.application.Container;

@Loader
public class AppContainer extends Container {

	public AppContainer() {
		this.install(App.class);
	}

}
