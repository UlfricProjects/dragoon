package com.ulfric.dragoon.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.DragoonTestSuite;

import java.lang.reflect.Field;

@RunWith(JUnitPlatform.class)
class ContainerTest extends DragoonTestSuite {

	private Container container;

	@BeforeEach
	void setup() {
		this.container = new Container();
	}

	@AfterEach
	void teardown() {
		ApplicationExample.last = null;

		if (container.isRunning()) {
			container.shutdown();
		}
	}

	@Test
	void testStart() {
		Truth.assertThat(this.container.isRunning()).isFalse();
		this.container.boot();
		Truth.assertThat(this.container.isRunning()).isTrue();
	}

	@Test
	void testShutdown() {
		this.container.boot();
		this.container.shutdown();
		Truth.assertThat(this.container.isRunning()).isFalse();
	}

	@Test
	void testInstall() {
		this.container.boot();
		this.container.install(ApplicationExample.class);
		this.container.shutdown();
		Truth.assertThat(ApplicationExample.last).isNotNull();
	}

	@Test
	void testStartOnRequested() {
		this.container = factory.request(Container.class);
		Truth.assertThat(this.container.isRunning()).isFalse();
		this.container.boot();
		Truth.assertThat(this.container.isRunning()).isTrue();
	}

	@Test
	void testShutdownOnRequested() {
		this.container = factory.request(Container.class);
		this.container.boot();
		this.container.shutdown();
		Truth.assertThat(this.container.isRunning()).isFalse();
	}

	@Test
	void testInstallTwice() {
		this.container.boot();
		Truth.assertThat(this.container.install(ApplicationExample.class).isSuccess()).isTrue();
		Truth.assertThat(this.container.install(ApplicationExample.class).isSuccess()).isFalse();
	}

	@Test
	void testInstallBadApplication() {
		this.container.boot();
		Truth.assertThat(this.container.install(BadApplication.class).isSuccess()).isFalse();
	}

	@Test
	void testInstallSelf() {
		this.container.boot();
		Truth.assertThat(this.container.install(this.container.getClass()).isSuccess()).isFalse();
	}

	@Test
	void testGetName() throws Exception {
		Field idCounter = Container.class.getDeclaredField("ID_COUNTER");
		idCounter.setAccessible(true);
		String id = String.valueOf(idCounter.get(null));
		Truth.assertThat(this.container.getName()).isEqualTo("Container#" + id);
	}

	static class ApplicationExample extends Application {
		static ApplicationExample last;

		ApplicationExample() {
			ApplicationExample.last = this;
		}
	}

	static class BadApplication extends Application {
		BadApplication() {
			throw new RuntimeException();
		}
	}

}
