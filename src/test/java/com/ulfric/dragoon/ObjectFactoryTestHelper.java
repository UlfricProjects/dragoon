package com.ulfric.dragoon;

import org.mockito.Mockito;

import java.util.logging.Logger;

public class ObjectFactoryTestHelper {

	public static ObjectFactory newObjectFactory() {
		ObjectFactory factory = new ObjectFactory();
		factory.bind(Logger.class).toValue(Mockito.mock(Logger.class));
		return factory;
	}

	private ObjectFactoryTestHelper() {}

}
