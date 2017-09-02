package com.ulfric.dragoon.application;

public class ManagedContainer extends Container implements AutoCloseable {

	public ManagedContainer() {
		state.setup();
	}

	@Override
	public final void close() {
		shutdown();
	}

}