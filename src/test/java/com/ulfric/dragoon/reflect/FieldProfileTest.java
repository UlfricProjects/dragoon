package com.ulfric.dragoon.reflect;

import com.google.common.truth.Truth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.inject.Inject;

@RunWith(JUnitPlatform.class)
class FieldProfileTest {

	private ObjectFactory factory;
	private FieldProfile.Builder builder;

	@BeforeEach
	void setup()
	{
		this.factory = new ObjectFactory();
		this.builder = FieldProfile.builder()
				.setFactory(this.factory)
				.setFlagToSearchFor(Inject.class);
	}

	@Test
	void testDefaultFilterIsNothing()
	{
		InjectMe inject = new InjectMe();
		this.builder.build().accept(inject);
		Truth.assertThat(inject.object).isNotNull();
	}

	@Test
	void testIgnoresNullValues()
	{
		InjectMe inject = new InjectMe();
		this.factory.bind(Object.class).to(NoInstances.class);
		this.builder.build().accept(inject);
		Truth.assertThat(inject.object).isNull();
	}

	static class InjectMe
	{
		@Inject
		Object object;
	}

	static class NoInstances
	{
		NoInstances()
		{
			throw new RuntimeException();
		}
	}

}