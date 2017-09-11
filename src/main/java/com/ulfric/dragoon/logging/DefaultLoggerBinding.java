package com.ulfric.dragoon.logging;

import java.util.function.Function;
import java.util.logging.Logger;

import com.ulfric.dragoon.Parameters;
import com.ulfric.dragoon.qualifier.EmptyQualifier;
import com.ulfric.dragoon.qualifier.Qualifier;

public enum DefaultLoggerBinding implements Function<Parameters, Logger> {

	INSTANCE;

	@Override
	public Logger apply(Parameters parameters) {
		Qualifier qualifier = parameters.getQualifier();
		if (qualifier == EmptyQualifier.INSTANCE) {
			return Logger.getGlobal();
		}

		String name = qualifier.getName();
		if (name == null) {
			return Logger.getGlobal();
		}

		return Logger.getLogger(name);
	}

}