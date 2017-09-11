package com.ulfric.dragoon;

public interface Factory {

	<T> T request(Class<T> type);

	<T> T request(Class<T> type, Parameters parameters);

}
