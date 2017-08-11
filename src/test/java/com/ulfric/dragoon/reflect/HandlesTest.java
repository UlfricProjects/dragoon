package com.ulfric.dragoon.reflect;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.UtilityTest;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

class HandlesTest extends UtilityTest {

	public HandlesTest() {
		super(Handles.class);
	}

	@Test
	void testGeneric() throws Throwable {
		MethodHandle method =
		        MethodHandles.lookup().findVirtual(HandlesTest.class, "hello", MethodType.methodType(String.class));

		Truth.assertThat(Handles.generic(method).invokeExact((Object) this)).isEqualTo("hello");
	}

	public String hello() {
		return "hello";
	}

}
