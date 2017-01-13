package com.ulfric.commons.cdi.construct;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ObjectFactoryTest {

	@Test
	void testNew()
	{
		Verify.that(ObjectFactory::newInstance).runsWithoutExceptions();
	}

}