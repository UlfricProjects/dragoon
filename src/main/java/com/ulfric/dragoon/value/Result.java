package com.ulfric.dragoon.value;

public enum Result {

	DELAYED,
	SUCCESS,
	FAILURE;

	public final boolean isSuccess() {
		return this != FAILURE;
	}

}
