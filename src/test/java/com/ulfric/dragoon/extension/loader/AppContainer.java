package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.application.Container;

public class AppContainer extends Container {

	public AppContainer() {
		install(App.class);
	}

}
