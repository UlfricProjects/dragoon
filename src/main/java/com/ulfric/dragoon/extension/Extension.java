package com.ulfric.dragoon.extension;

public abstract class Extension {

	public <T> Class<T> transform(Class<T> type)
	{
		return type;
	}

	public <T> T transform(T value)
	{
		return value;
	}

}