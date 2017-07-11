package com.ulfric.dragoon.extension;

import com.google.common.truth.Truth;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class ExtensionTest {

	@Test
	void testTransformClassDefaultsToIdentity()
	{
		Truth.assertThat(new Extension() { }.transform(Object.class)).isSameAs(Object.class);
	}

	@Test
	void testTransformValueDefaultsToIdentity()
	{
		Object object = new Object();
		Truth.assertThat(new Extension() { }.transform(object)).isSameAs(object);
	}

}