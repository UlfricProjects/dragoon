package com.ulfric.dragoon.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Application implements Hookable {

	private final List<Runnable> boot = new ArrayList<>();
	private final List<Runnable> shutdown = new ArrayList<>();
	private boolean running;

	public final boolean isRunning() {
		return running;
	}

	public final void boot() {
		if (isRunning()) {
			return;
		}

		running = true;
		boot.forEach(Runnable::run);
	}

	public final void shutdown() {
		if (!isRunning()) {
			return;
		}

		running = false;
		shutdown.forEach(Runnable::run);
	}

	@Override
	public final void addBootHook(Runnable hook) {
		Objects.requireNonNull(hook, "hook");
		boot.add(hook);
	}

	@Override
	public final void addShutdownHook(Runnable hook) {
		Objects.requireNonNull(hook, "hook");
		shutdown.add(hook);
	}

}
