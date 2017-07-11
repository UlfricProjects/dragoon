package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.application.Container;

@Loader
public class AppContainer extends Container {

	@Override
	public void setup() {
		this.install(App.class);
	}

}
