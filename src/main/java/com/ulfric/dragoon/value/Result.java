package com.ulfric.dragoon.value;

public interface Result {

	Result DELAYED = () -> true;
	Result SUCCESS = () -> true;
	Result FAILURE = () -> false;

	boolean isSuccess();

}
