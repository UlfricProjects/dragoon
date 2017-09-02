package com.ulfric.dragoon.extension;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

class ExtensionTest {

	@Test
	void testTransformClassDefaultsToIdentity() {
		Truth.assertThat(new Extension() {}.transform(Object.class)).isSameAs(Object.class);
	}

	@Test
	void testTransformValueDefaultsToIdentity() {
		Object object = new Object();
		Truth.assertThat(new Extension() {}.transform(object)).isSameAs(object);
	}

}
