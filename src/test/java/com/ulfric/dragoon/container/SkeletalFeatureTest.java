package com.ulfric.dragoon.container;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class SkeletalFeatureTest {

	private ObjectFactory factory;
	private Container parent;
	private Feature child;

	@BeforeEach
	void init()
	{
		this.factory = ObjectFactory.newInstance();
		this.factory.bind(Logger.class).to(NullLogger.class);
		this.parent = this.factory.requestExact(Container.class);
		this.parent.install(TestingFeature.class);
		this.child = TestingFeature.lastInstance;
	}

	@Test
	void testParentLoad_LoadsChild()
	{
		Verify.that(this.child.isLoaded()).isFalse();
		this.parent.load();
		Verify.that(this.child.isLoaded()).isTrue();
	}

	@Test
	void testParentEnable_EnablesChild()
	{
		Verify.that(this.child.isEnabled()).isFalse();
		this.parent.enable();
		Verify.that(this.child.isEnabled()).isTrue();
	}

	@Test
	void testParentDisable_DisableChild()
	{
		this.parent.enable();
		Verify.that(this.child.isDisabled()).isFalse();
		this.parent.disable();
		Verify.that(this.child.isDisabled()).isTrue();
	}

	static class TestingFeature extends SkeletalFeature
	{
		static TestingFeature lastInstance;

		public TestingFeature()
		{
			TestingFeature.lastInstance = this;
		}
	}

}