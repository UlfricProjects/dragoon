package com.ulfric.dragoon.container;

import com.ulfric.commons.naming.Name;
import com.ulfric.verify.Verify;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class FeatureTest {

	private final Feature feature = new FeatureImpl();
	private final FeatureNamed featureNamed = new FeatureNamed();

	@Test
	void testIsDisabled_invertsIsEnabled()
	{
		Verify.that(this.feature.isDisabled()).isFalse();
	}

	@Test
	void testGetName_className()
	{
		Verify.that(this.feature.getName()).isEqualTo(FeatureImpl.class.getSimpleName());
	}

	@Test
	void testGetName_named()
	{
		Verify.that(this.featureNamed.getName()).isEqualTo("name");
	}

	static final class FeatureImpl implements Feature
	{
		@Override
		public boolean isEnabled()
		{
			return true;
		}

		@Override
		public void enable()
		{

		}

		@Override
		public void disable()
		{

		}
	}

	@Name("name")
	static final class FeatureNamed implements Feature
	{
		@Override
		public boolean isEnabled()
		{
			return true;
		}

		@Override
		public void enable()
		{

		}

		@Override
		public void disable()
		{

		}
	}

}
