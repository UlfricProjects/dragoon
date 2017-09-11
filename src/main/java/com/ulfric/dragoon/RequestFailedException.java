package com.ulfric.dragoon;

public class RequestFailedException extends RuntimeException {

	public RequestFailedException(Class<?> type, Parameters parameters, Exception cause) {
		super("Request failed: Type: {" + type + "} Parameters: {" + parameters + '}', cause);
	}

}
