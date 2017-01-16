package com.ulfric.commons.cdi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class InjectorTest {

	private ObjectFactory factory;
	private Injector injector;

	@BeforeEach
	void testNew()
	{
		this.factory = ObjectFactory.newInstance();
		this.injector = new Injector(this.factory);
	}

}