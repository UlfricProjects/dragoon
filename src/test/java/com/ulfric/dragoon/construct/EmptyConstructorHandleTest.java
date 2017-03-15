package com.ulfric.dragoon.construct;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class EmptyConstructorHandleTest {

	@Test
	void test_fakeEnumTestRequirements()
	{
		EmptyConstructorHandle.values();
		EmptyConstructorHandle.valueOf("INSTANCE");
	}

}