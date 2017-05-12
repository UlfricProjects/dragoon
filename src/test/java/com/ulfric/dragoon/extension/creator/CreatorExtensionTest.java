package com.ulfric.dragoon.extension.creator;

import com.google.common.truth.Truth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.ObjectFactory;

@RunWith(JUnitPlatform.class)
class CreatorExtensionTest {

	private ObjectFactory factory;
	private CreatorExtension extension;

	@BeforeEach
	void setup()
	{
		this.factory = new ObjectFactory();
		this.extension = new CreatorExtension(this.factory);
	}

	@Test
	void testInjectsCreator()
	{
		Truth.assertThat(this.transform(new InjectCreatorPlease()).creator).isNotNull();
	}

	@Test
	void testInjectsCreatorIfFieldTypeIsObject()
	{
		Truth.assertThat(this.transform(new InjectCreatorPleaseObject()).creator).isNotNull();
	}

	@Test
	void testInjectsCreatorIfFieldTypeIsNotFactory()
	{
		Truth.assertThat(this.transform(new DoNotInjectCreatorPlease()).creator).isNull();
	}

	private <T> T transform(T value)
	{
		return this.extension.transform(value);
	}

	static class InjectCreatorPlease
	{
		@Creator
		Factory creator;
	}

	static class InjectCreatorPleaseObject
	{
		@Creator
		Object creator;
	}

	static class DoNotInjectCreatorPlease
	{
		@Creator
		Integer creator;
	}

}