package com.ulfric.dragoon.value;

public interface Result {

	Result SUCCESS = () -> true;
	Result FAILURE = () -> false;

	boolean isSuccess();

}
