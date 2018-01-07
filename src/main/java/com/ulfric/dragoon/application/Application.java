package com.ulfric.dragoon.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.reflect.Classes;

public class Application implements Hookable { // TODO unit tests for crash hooks

	private final List<Supplier<Throwable>> boot = new ArrayList<>();
	private final List<Supplier<Throwable>> shutdown = new ArrayList<>();
	private final List<Consumer<Crash>> crash = new ArrayList<>();
	private ApplicationState state = ApplicationState.STATELESS;

	@Inject(optional = true)
	private Logger logger;

	public final ApplicationState getState() {
		return state;
	}

	public final void boot() {
		if (state != ApplicationState.STATELESS) {
			return;
		}

		if (!testBootability()) {
			return; // TODO should we log this?
		}

		state = ApplicationState.BOOT;
		Optional<Crash> crash = callBootHooks();
		if (crash.isPresent()) {
			crash(crash.get());
		} else {
			state = ApplicationState.RUNTIME;
		}
	}

	private boolean testBootability() {
		if (this instanceof CheckedBoot) {
			CheckedBoot checked = (CheckedBoot) this;
			return checked.canBoot();
		}
		return true;
	}

	private Optional<Crash> callBootHooks() {
		return callHooks(boot);
	}

	public final void shutdown() {
		if (state != ApplicationState.RUNTIME) {
			return;
		}

		state = ApplicationState.SHUTDOWN;
		Optional<Crash> crash = callShutdownHooks();
		state = ApplicationState.STATELESS;
		if (crash.isPresent()) {
			crash(crash.get());
		}
	}

	private Optional<Crash> callShutdownHooks() {
		return callHooks(shutdown);
	}

	private Optional<Crash> callHooks(List<Supplier<Throwable>> hooks) {
		if (hooks.isEmpty()) {
			return Optional.empty();
		}

		List<Throwable> thrown = hooks.stream()
				.map(Supplier::get)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		if (thrown.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(Crash.builder().setCauses(thrown).setState(state).build());
	}

	private final void crash(Crash crash) {
		logCrash(crash);
		callCrashHooks(crash);
		shutdown();
	}

	private void logCrash(Crash crash) {
		if (logger == null) {
			return;
		}
		for (Throwable thrown : crash.getCauses()) {
			logger.log(Level.SEVERE, getName() + " crashed in " + crash.getState(), thrown);
		}
	}

	private void callCrashHooks(Crash crash) {
		if (this.crash.isEmpty()) {
			return;
		}

		this.crash.forEach(hook -> hook.accept(crash));
	}

	@Override
	public final void addBootHook(Runnable hook) {
		Objects.requireNonNull(hook, "hook");

		boot.add(checked(hook));
	}

	@Override
	public final void addShutdownHook(Runnable hook) {
		Objects.requireNonNull(hook, "hook");

		shutdown.add(checked(hook));
	}

	protected final void addCrashHook(Runnable runnable) {
		addCrashHook(ignore -> runnable.run());
	}

	protected final void addCrashHook(Consumer<Crash> hook) {
		Objects.requireNonNull(hook, "hook");

		 crash.add(checked(hook));
	}

	private Supplier<Throwable> checked(Runnable runnable) {
		return () -> {
			try {
				runnable.run();
				return null;
			} catch (Throwable exception) {
				return exception;
			}
		};
	}

	private Consumer<Crash> checked(Consumer<Crash> consumer) {
		return crash -> {
			try {
				consumer.accept(crash);
			} catch (Throwable exception) {
				if (logger != null) {
					logger.log(Level.SEVERE, "Crash hook threw an exception", exception);
				}
			}
		};
	}

	private String getName() {
		return Classes.getNonDynamic(getClass()).getSimpleName();
	}

}
