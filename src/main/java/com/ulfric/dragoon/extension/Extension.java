package com.ulfric.dragoon.extension;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.extension.inject.Inject;

public abstract class Extension extends Application {

	public static final int HIGH_PRIORITY = 10;
	public static final int NORMAL_PRIORITY = 5;
	public static final int LOW_PRIORITY = 1;

	@Inject
	private ObjectFactory factory;

	public Extension() {
		addBootHook(this::installThisInFactory);
		addShutdownHook(this::uninstallThisInFactory);
	}

	private void installThisInFactory() {
		factory.bind(getClass()).toValue(this);
		factory.install(getClass());
	}

	public void uninstallThisInFactory() {
		factory.uninstall(getClass());
	}

	public <T> Class<? extends T> transform(Class<T> type) {
		return type;
	}

	public <T> T transform(T value) {
		return value;
	}

	public int getPriority() {
		return NORMAL_PRIORITY;
	}

}
