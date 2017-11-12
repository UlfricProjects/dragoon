package com.ulfric.dragoon;

public enum NothingFactory implements Factory {

	INSTANCE;

	@Override
	public <T> T request(Class<T> type) {
		return null;
	}

	@Override
	public <T> T request(Class<T> type, Parameters parameters) {
		return null;
	}

}
