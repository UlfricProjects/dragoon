package com.ulfric.dragoon.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ulfric.dragoon.extension.inject.Inject;

public class Log {

	@Inject(optional = true)
	private Logger logger;

	public void info(String message) {
		if (logger != null) {
			logger.info(message);
		}
	}

	public void warning(String message) {
		if (logger != null) {
			logger.warning(message);
		}
	}

	public void severe(String message) {
		if (logger != null) {
			logger.severe(message);
		}
	}

	public void log(Level level, String message, Throwable thrown) {
		if (logger != null) {
			logger.log(level, message, thrown);
		}
	}

}
