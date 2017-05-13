package com.ulfric.dragoon;

import com.google.common.truth.Truth;

import org.junit.jupiter.api.Test;

import com.ulfric.dragoon.reflect.Instances;

public abstract class UtilityTest {

	private final Class<?> utility;

	public UtilityTest(Class<?> utility)
	{
		this.utility = utility;
	}

	@Test
	void testConstructor()
	{
		Truth.assertThat(Instances.newInstance(this.utility)).isNotNull();
	}

}