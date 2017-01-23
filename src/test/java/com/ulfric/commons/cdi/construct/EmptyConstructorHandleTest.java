package com.ulfric.commons.cdi.construct;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.construct.EmptyConstructorHandle;

@RunWith(JUnitPlatform.class)
public class EmptyConstructorHandleTest {

	@Test
	void test_fakeEnumTestRequirements()
	{
		EmptyConstructorHandle.values();
		EmptyConstructorHandle.valueOf("INSTANCE");
	}

}