package com.ulfric.dragoon.application;

public interface Hookable {

	void addBootHook(Runnable hook);

	void addShutdownHook(Runnable hook);

}