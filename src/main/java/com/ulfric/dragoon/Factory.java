package com.ulfric.dragoon;

public interface Factory {

	Binding bind(Class<?> request);

	Object request(Class<?> request);

}