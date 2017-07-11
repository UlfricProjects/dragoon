package com.ulfric.dragoon.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

@RunWith(JUnitPlatform.class)
class ApplicationTest {

	private Application application;

	@BeforeEach
	void setup() {
		this.application = new Application();
	}

	@Test
	void testStart() {
		Truth.assertThat(this.application.isRunning()).isFalse();
		this.application.start();
		Truth.assertThat(this.application.isRunning()).isTrue();
	}

	@Test
	void testShutdown() {
		this.application.start();
		Truth.assertThat(this.application.isRunning()).isTrue();
		this.application.shutdown();
		Truth.assertThat(this.application.isRunning()).isFalse();
	}

	@Test
	void testStartIfRunning() {
		this.application.start();
		this.application.start();
		Truth.assertThat(this.application.isRunning()).isTrue();
	}

	@Test
	void testShutdownIfNotRunning() {
		Truth.assertThat(this.application.isRunning()).isFalse();
		this.application.shutdown();
		Truth.assertThat(this.application.isRunning()).isFalse();
	}

	@Test
	void testStartHook() {
		boolean[] ran = new boolean[1];
		this.application.addStartHook(() -> ran[0] = true);
		this.application.start();
		Truth.assertThat(ran[0]).isTrue();
	}

	@Test
	void testShutdownHook() {
		boolean[] ran = new boolean[1];
		this.application.addShutdownHook(() -> ran[0] = true);
		this.application.start();
		this.application.shutdown();
		Truth.assertThat(ran[0]).isTrue();
	}

}
