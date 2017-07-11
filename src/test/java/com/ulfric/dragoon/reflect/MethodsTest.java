package com.ulfric.dragoon.reflect;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.UtilityTest;

@RunWith(JUnitPlatform.class)
class MethodsTest extends UtilityTest {

	public MethodsTest() {
		super(Methods.class);
	}

	@Test
	void testGetOverridableMethods() {
		Truth.assertThat(Methods.getOverridableMethods(B.class)).hasSize(2);
	}

	static class A {
		void hello() {}
	}

	static class B extends A {
		void goodbye() {}
	}

}
