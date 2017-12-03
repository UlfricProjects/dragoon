package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class CurrentThreadExecutor extends AbstractExecutorService {

	@Override
	public void shutdown() {
	}

	@Override
	public List<Runnable> shutdownNow() {
		return Collections.emptyList();
	}

	@Override
	public boolean isShutdown() {
		return false;
	}

	@Override
	public boolean isTerminated() {
		return false;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) {
		return true;
	}

	@Override
	public void execute(Runnable command) {
		command.run();
	}

}
