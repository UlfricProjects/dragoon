package com.ulfric.dragoon.application;

import com.google.common.truth.Truth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.ObjectFactory;

@RunWith(JUnitPlatform.class)
class ContainerTest {

	private Container container;

	@BeforeEach
	void setup()
	{
		this.container = new Container();
	}

	@AfterEach
	void teardown()
	{
		ApplicationExample.last = null;
	}

	@Test
	void testStart()
	{
		Truth.assertThat(this.container.isRunning()).isFalse();
		this.container.start();
		Truth.assertThat(this.container.isRunning()).isTrue();
	}

	@Test
	void testShutdown()
	{
		this.container.start();
		this.container.shutdown();
		Truth.assertThat(this.container.isRunning()).isFalse();
	}

	@Test
	void testInstall()
	{
		this.container.start();
		this.container.install(ApplicationExample.class);
		this.container.shutdown();
		Truth.assertThat(ApplicationExample.last).isNotNull();
	}

	@Test
	void testStartOnRequested()
	{
		this.container = new ObjectFactory().request(Container.class);
		Truth.assertThat(this.container.isRunning()).isFalse();
		this.container.start();
		Truth.assertThat(this.container.isRunning()).isTrue();
	}

	@Test
	void testShutdownOnRequested()
	{
		this.container = new ObjectFactory().request(Container.class);
		this.container.start();
		this.container.shutdown();
		Truth.assertThat(this.container.isRunning()).isFalse();
	}

	@Test
	void testInstallTwice()
	{
		Truth.assertThat(this.container.install(ApplicationExample.class).isSuccess()).isTrue();
		Truth.assertThat(this.container.install(ApplicationExample.class).isSuccess()).isFalse();
	}

	@Test
	void testInstallBadApplication()
	{
		Truth.assertThat(this.container.install(BadApplication.class).isSuccess()).isFalse();
	}

	@Test
	void testInstallSelf()
	{

		Truth.assertThat(this.container.install(this.container.getClass()).isSuccess()).isFalse();
	}

	static class ApplicationExample extends Application
	{
		static ApplicationExample last;

		ApplicationExample()
		{
			ApplicationExample.last = this;
		}
	}

	static class BadApplication extends Application
	{
		BadApplication()
		{
			throw new RuntimeException();
		}
	}

}