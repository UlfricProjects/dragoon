package com.ulfric.dragoon;

import java.util.logging.Logger;

import com.ulfric.dragoon.container.NullLogger;

public class TestObjectFactory {

	public static ObjectFactory newInstance()
	{
		ObjectFactory factory = ObjectFactory.newInstance();
		factory.bind(Logger.class).to(NullLogger.class);
		return factory;
	}

	private TestObjectFactory()
	{
		throw new IllegalStateException();
	}

}