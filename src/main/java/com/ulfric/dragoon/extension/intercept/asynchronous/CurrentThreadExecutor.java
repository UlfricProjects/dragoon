package com.ulfric.dragoon.extension.intercept.asynchronous;

import java.util.concurrent.Executor;

public class CurrentThreadExecutor implements Executor {

	@Override
	public void execute(Runnable command) {
		command.run();
	}

}
