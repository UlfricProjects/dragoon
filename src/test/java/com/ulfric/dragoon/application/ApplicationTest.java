package com.ulfric.dragoon.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

class ApplicationTest {

	private Application application;

	@BeforeEach
	void setup() {
		this.application = new Application();
	}

	@Test
	void testStart() {
		Truth.assertThat(this.application.getState()).isSameAs(ApplicationState.STATELESS);
		this.application.boot();
		Truth.assertThat(this.application.getState()).isSameAs(ApplicationState.RUNTIME);
	}

	@Test
	void testShutdown() {
		this.application.boot();
		this.application.shutdown();
		Truth.assertThat(this.application.getState()).isSameAs(ApplicationState.STATELESS);
	}

	@Test
	void testStartIfRunning() {
		this.application.boot();
		this.application.boot();
		Truth.assertThat(this.application.getState()).isSameAs(ApplicationState.RUNTIME);
	}

	@Test
	void testShutdownIfNotRunning() {
		this.application.shutdown();
		Truth.assertThat(this.application.getState()).isSameAs(ApplicationState.STATELESS);
	}

	@Test
	void testStartHook() {
		boolean[] ran = new boolean[1];
		this.application.addBootHook(() -> ran[0] = true);
		this.application.boot();
		Truth.assertThat(ran[0]).isTrue();
	}

	@Test
	void testShutdownHook() {
		boolean[] ran = new boolean[1];
		this.application.addShutdownHook(() -> ran[0] = true);
		this.application.boot();
		this.application.shutdown();
		Truth.assertThat(ran[0]).isTrue();
	}

}
