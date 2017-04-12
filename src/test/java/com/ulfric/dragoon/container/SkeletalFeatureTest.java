package com.ulfric.dragoon.container;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.TestObjectFactory;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class SkeletalFeatureTest {

	private ObjectFactory factory;
	private Container parent;

	@BeforeEach
	void init()
	{
		Child.instance = null;
		this.factory = TestObjectFactory.newInstance();
		this.parent = this.factory.requestExact(Container.class);
		this.parent.install(Child.class);
	}

	@Test
	void testParentEnable_EnablesChild()
	{
		Verify.that(Child.instance).isNull();
		this.parent.enable();
		Verify.that(Child.instance.isEnabled()).isTrue();
	}

	@Test
	void testParentDisable_DisableChild()
	{
		this.parent.enable();
		Verify.that(Child.instance.isDisabled()).isFalse();
		this.parent.disable();
		Verify.that(Child.instance.isDisabled()).isTrue();
	}

	static class Child extends SkeletalFeature
	{
		static Child instance;

		public Child()
		{
			Child.instance = this;
		}
	}

}