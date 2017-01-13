package com.ulfric.commons.cdi.construct;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ObjectFactoryTest {

	private ObjectFactory factory;

	@BeforeEach
	void init()
	{
		this.factory = ObjectFactory.newInstance();
	}

	@Test
	void testNewInstance()
	{
		Verify.that(ObjectFactory::newInstance).suppliesUniqueValues();
	}

	@Test
	void testCreateSubfactory()
	{
		Verify.that(this.factory::subfactory).suppliesUniqueValues();
	}

}