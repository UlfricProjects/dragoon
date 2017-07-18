package com.ulfric.dragoon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.extension.Extension;

import java.util.function.Function;

@RunWith(JUnitPlatform.class)
class ObjectFactoryTest extends DragoonTestSuite {

	@Test
	void testRequest() {
		Truth.assertThat(factory.request(Example.class)).isInstanceOf(Example.class);
	}

	@Test
	void testRequestReturnsNull() {
		Truth.assertThat(factory.request(NoInstances.class)).isNull();
	}

	@Test
	void testRequestIncompatible() {
		factory.bind(NoInstances.class).to(Object.class);
		Assertions.assertThrows(RequestFailedException.class, () -> factory.request(NoInstances.class));
	}

	@Test
	void testRequestIncompatibleButUnspecific() {
		factory.bind(NoInstances.class).to(Object.class);
		Truth.assertThat(factory.requestUnspecific(NoInstances.class)).isNotNull();
	}

	@Test
	void testBind() {
		factory.bind(Object.class).to(Example.class);
		Truth.assertThat(factory.request(Object.class)).isInstanceOf(Example.class);
	}

	@Test
	void testBindToSelf() {
		factory.bind(Object.class).to(Object.class);
		Truth.assertThat(factory.request(Object.class)).isInstanceOf(Object.class);
	}

	@Test
	void testBindToFunction() {
		Object value = new Object();
		Function<Object[], ?> function = ignore -> value;
		factory.bind(Object.class).toFunction(function);
		Truth.assertThat(factory.request(Object.class)).isSameAs(value);
	}

	@Test
	void testBindToValue() {
		Object value = new Object();
		factory.bind(Object.class).toValue(value);
		Truth.assertThat(factory.request(Object.class)).isSameAs(value);
	}

	@Test
	void testBindRemoval() {
		factory.bind(Object.class).to(Example.class);
		factory.bind(Object.class).toNothing();
		Truth.assertThat(factory.request(Object.class)).isInstanceOf(Object.class);
	}

	@Test
	void testBindNone() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> factory.bind());
	}

	@Test
	void testInstall() {
		Truth.assertThat(factory.install(ExampleExtension.class).isSuccess()).isTrue();
	}

	@Test
	void testDoubleInstall() {
		factory.install(ExampleExtension.class);
		Truth.assertThat(factory.install(ExampleExtension.class).isSuccess()).isFalse();
	}

	@Test
	void testFailingInstallation() {
		Truth.assertThat(factory.install(Extension.class).isSuccess()).isFalse();
	}

	static class ExampleExtension extends Extension {

	}

	static class Example {

	}

	static class NoInstances {
		NoInstances() {
			throw new RuntimeException();
		}
	}

}
