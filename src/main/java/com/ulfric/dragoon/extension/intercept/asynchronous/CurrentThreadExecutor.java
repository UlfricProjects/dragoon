package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.util.concurrent.Executor;

public enum CurrentThreadExecutor implements Executor {

	INSTANCE;

	@Override
	public void execute(Runnable command) {
		command.run();
	}

}
