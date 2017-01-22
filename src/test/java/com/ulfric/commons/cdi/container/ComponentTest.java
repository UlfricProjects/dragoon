package com.ulfric.commons.cdi.container;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.naming.Name;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ComponentTest {

	private final ComponentImpl component = new ComponentImpl();
	private final ComponentNamed componentNamed = new ComponentNamed();

	@Test
	void testIsUnloaded_invertsIsLoaded()
	{
		Verify.that(this.component.isUnloaded()).isTrue();
	}

	@Test
	void testIsDisabled_invertsIsEnabled()
	{
		Verify.that(this.component.isDisabled()).isFalse();
	}

	@Test
	void testGetName_className()
	{
		Verify.that(this.component.getName()).isEqualTo(ComponentImpl.class.getSimpleName());
	}

	@Test
	void testGetName_named()
	{
		Verify.that(this.componentNamed.getName()).isEqualTo("name");
	}

	@Test
	void testIsUnloaded_whenFalse()
	{
		Verify.that(this.componentNamed.isUnloaded()).isFalse();
	}

	static final class ComponentImpl implements Component
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

	@Name(value = "name")
	static final class ComponentNamed implements Component
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
