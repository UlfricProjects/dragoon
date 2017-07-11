package com.ulfric.dragoon;

public interface Factory {

	<T> T request(Class<T> type);

}
