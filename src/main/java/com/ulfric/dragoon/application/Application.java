package com.ulfric.dragoon.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Application {

	private final List<Runnable> start = new ArrayList<>();
	private final List<Runnable> shutdown = new ArrayList<>();
	private boolean running;

	public final boolean isRunning() {
		return this.running;
	}

	public final void start() {
		if (this.isRunning()) {
			return;
		}

		this.running = true;
		this.start.forEach(Runnable::run);
	}

	public final void shutdown() {
		if (!this.isRunning()) {
			return;
		}

		this.running = false;
		this.shutdown.forEach(Runnable::run);
	}

	public final void addStartHook(Runnable hook) {
		Objects.requireNonNull(hook, "hook");
		this.start.add(hook);
	}

	public final void addShutdownHook(Runnable hook) {
		Objects.requireNonNull(hook, "hook");
		this.shutdown.add(hook);
	}

}
