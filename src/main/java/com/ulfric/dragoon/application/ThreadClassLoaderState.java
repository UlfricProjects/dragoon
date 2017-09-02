package com.ulfric.dragoon.application;

import java.util.Objects;

public final class ThreadClassLoaderState {

	private final ClassLoader desiredLoader;
	private Thread knownThread;
	private ClassLoader oldClassLoader;

	public ThreadClassLoaderState(ClassLoader desiredLoader) {
		Objects.requireNonNull(desiredLoader, "desiredLoader");

		this.desiredLoader = desiredLoader;
	}

	public void setup() {
		knownThread = Thread.currentThread();
		oldClassLoader = knownThread.getContextClassLoader();
		knownThread.setContextClassLoader(desiredLoader);
	}

	public void teardown() {
		if (knownThread.getContextClassLoader() == desiredLoader) {
			knownThread.setContextClassLoader(oldClassLoader);

			cleanupKnownContext();
		}
	}

	private void cleanupKnownContext() {
		knownThread = null;
		oldClassLoader = null;
	}

	public void doContextual(Runnable runnable) {
		ThreadClassLoaderState secondary = new ThreadClassLoaderState(desiredLoader);

		try {
			secondary.setup();
			runnable.run();
		} finally {
			secondary.teardown();
		}
	}

}