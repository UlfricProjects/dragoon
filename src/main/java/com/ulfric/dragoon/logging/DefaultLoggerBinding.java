package com.ulfric.dragoon.logging;

import com.ulfric.dragoon.reflect.NameHelper;

import java.util.function.Function;
import java.util.logging.Logger;

public enum DefaultLoggerBinding implements Function<Object[], Logger> {

	INSTANCE;

	@Override
	public Logger apply(Object[] arguments) {
		if (arguments.length == 0) {
			return Logger.getGlobal();
		}

		String name = NameHelper.getName(arguments);
		if (name == null) {
			return Logger.getGlobal();
		}

		return Logger.getLogger(name);
	}

}