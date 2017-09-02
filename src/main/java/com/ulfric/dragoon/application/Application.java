package com.ulfric.dragoon.application;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
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
		callBootHooks();
	}

	private void callBootHooks() {
		boot.forEach(Runnable::run);
	}

	public final void shutdown() {
		if (!isRunning()) {
			return;
		}

		running = false;
		callShutdownHooks();
	}

	private void callShutdownHooks() {
		if (shutdown.isEmpty()) {
			return;
		}

		ListIterator<Runnable> reverseIterator = shutdown.listIterator(shutdown.size());
		while (reverseIterator.hasPrevious()) {
			reverseIterator.previous().run();
		}
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
