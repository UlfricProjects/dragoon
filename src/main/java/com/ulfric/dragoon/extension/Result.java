package com.ulfric.dragoon.extension;

public interface Result {

	Result SUCCESS = () -> true;
	Result FAILURE = () -> false;

	boolean isSuccess();

}