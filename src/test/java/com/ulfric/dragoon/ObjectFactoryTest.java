package com.ulfric.dragoon;

import com.google.common.truth.Truth;

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
		Truth.assertThat(this.factory.requestNotNull(Example.class)).isInstanceOf(Example.class);
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

	static class ExampleExtension extends Extension
	{
		
	}

	static class Example
	{
		
	}

}