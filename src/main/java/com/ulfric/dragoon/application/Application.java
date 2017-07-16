package com.ulfric.dragoon.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Application {

	private final List<Runnable> boot = new ArrayList<>();
	private final List<Runnable> shutdown = new ArrayList<>();
	private boolean running;

	public final boolean isRunning() {
		return this.running;
	}

	public final void boot() {
		if (this.isRunning()) {
			return;
		}

		this.running = true;
		this.boot.forEach(Runnable::run);
	}

	public final void shutdown() {
		if (!this.isRunning()) {
			return;
		}

		this.running = false;
		this.shutdown.forEach(Runnable::run);
	}

	public final void addBootHook(Runnable hook) {
		Objects.requireNonNull(hook, "hook");
		this.boot.add(hook);
	}

	public final void addShutdownHook(Runnable hook) {
		Objects.requireNonNull(hook, "hook");
		this.shutdown.add(hook);
	}

}
