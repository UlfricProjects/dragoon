package com.ulfric.commons.cdi.container;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class SkeletalComponentTest {

	private Component parent;
	private Component child;

	@BeforeEach
	void init()
	{
		this.parent = Mockito.mock(Component.class);

		Mockito.when(this.parent.isLoaded()).thenReturn(true);
		Mockito.when(this.parent.isEnabled()).thenReturn(false);

		this.child = new SkeletalComponent(this.parent) {

			@Override
			public void enable()
			{

			}

			@Override
			public void disable()
			{

			}

		};
	}

	@Test
	void testIsLoaded_delegates()
	{
		Verify.that(this.child.isLoaded()).isTrue();
	}

	@Test
	void testIsEnabled_delegates()
	{
		Verify.that(this.child.isEnabled()).isFalse();
	}

	@Test
	void testIsDisabled_inverts()
	{
		Verify.that(this.child.isDisabled()).isTrue();
	}

	@Test
	void testLoad_iAmLiterallyJustDoingThisForTheCoverage()
	{
		Verify.that(() -> this.child.load()).runsWithoutExceptions();
	}

}
