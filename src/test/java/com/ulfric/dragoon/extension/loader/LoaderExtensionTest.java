package com.ulfric.dragoon.extension.loader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.DragoonTestSuite;
import com.ulfric.dragoon.application.Container;

@RunWith(JUnitPlatform.class)
class LoaderExtensionTest extends DragoonTestSuite {

	@AfterEach
	void teardown() {
		Apps.last = null;
	}

	@Test
	void test() {
		Apps.last = null;
		Container container = this.factory.request(AppContainer.class);
		container.boot();
		container.shutdown();

		Truth.assertThat(Apps.last).isSameAs(container);
	}

	static class InContainer {

	}

}
