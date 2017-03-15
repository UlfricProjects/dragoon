package com.ulfric.dragoon.container;

import com.ulfric.commons.naming.Name;
import com.ulfric.verify.Verify;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class FeatureTest {

	private final FeatureImpl feature = new FeatureImpl();
	private final FeatureNamed featureNamed = new FeatureNamed();

	@Test
	void testIsUnloaded_invertsIsLoaded()
	{
		Verify.that(this.feature.isUnloaded()).isTrue();
	}

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

	@Test
	void testIsUnloaded_whenFalse()
	{
		Verify.that(this.featureNamed.isUnloaded()).isFalse();
	}

	static final class FeatureImpl implements Feature
	{

		@Override
		public boolean isLoaded()
		{
			return false;
		}

		@Override
		public boolean isEnabled()
		{
			return true;
		}

		@Override
		public void load()
		{

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
		public boolean isLoaded()
		{
			return true;
		}

		@Override
		public boolean isEnabled()
		{
			return true;
		}

		@Override
		public void load()
		{

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
