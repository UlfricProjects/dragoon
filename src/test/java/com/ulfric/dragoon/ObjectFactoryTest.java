package com.ulfric.dragoon;

import com.google.common.truth.Truth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.extension.Extension;

@RunWith(JUnitPlatform.class)
class ObjectFactoryTest {

	private ObjectFactory factory;

	@BeforeEach
	void setup()
	{
		this.factory = new ObjectFactory();
	}

	@Test
	void testRequest()
	{
		Truth.assertThat(this.factory.request(Example.class)).isInstanceOf(Example.class);
	}

	@Test
	void testRequestReturnsNull()
	{
		Truth.assertThat(this.factory.request(NoInstances.class)).isNull();
	}

	@Test
	void testRequestIncompatible()
	{
		this.factory.bind(NoInstances.class).to(Object.class);
		Assertions.assertThrows(RequestFailedException.class, () -> this.factory.request(NoInstances.class));
	}

	@Test
	void testRequestIncompatibleButUnspecofoc()
	{
		this.factory.bind(NoInstances.class).to(Object.class);
		Truth.assertThat(this.factory.requestUnspecific(NoInstances.class)).isNotNull();
	}

	@Test
	void testBind()
	{
		this.factory.bind(Object.class).to(Example.class);
		Truth.assertThat(this.factory.request(Object.class)).isInstanceOf(Example.class);
	}

	@Test
	void testBindToSelf()
	{
		this.factory.bind(Object.class).to(Object.class);
		Truth.assertThat(this.factory.request(Object.class)).isInstanceOf(Object.class);
	}

	@Test
	void testBindRemoval()
	{
		this.factory.bind(Object.class).to(Example.class);
		this.factory.bind(Object.class).to(null);
		Truth.assertThat(this.factory.request(Object.class)).isInstanceOf(Object.class);
	}

	@Test
	void testInstall()
	{
		Truth.assertThat(this.factory.install(ExampleExtension.class).isSuccess()).isTrue();
	}

	@Test
	void testDoubleInstall()
	{
		this.factory.install(ExampleExtension.class);
		Truth.assertThat(this.factory.install(ExampleExtension.class).isSuccess()).isFalse();
	}

	@Test
	void testFailingInstallation()
	{
		Truth.assertThat(this.factory.install(Extension.class).isSuccess()).isFalse();
	}

	static class ExampleExtension extends Extension
	{
		
	}

	static class Example
	{
		
	}

	static class NoInstances
	{
		NoInstances()
		{
			throw new RuntimeException();
		}
	}

}