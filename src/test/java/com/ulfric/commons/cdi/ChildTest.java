package com.ulfric.commons.cdi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ChildTest {

	private Son child;

	@BeforeEach
	void init()
	{
		this.child = new Son();
	}

	@Test
	void testHasParent_root()
	{
		Verify.that(this.child.hasParent()).isFalse();
	}

	@Test
	void testHasParent_child()
	{
		Verify.that(new Son(this.child).hasParent()).isTrue();
	}

	@Test
	void testGetParent_root()
	{
		Verify.that(this.child.getParent()).isNull();
	}

	@Test
	void testGetParent_child()
	{
		Verify.that(new Son(this.child).getParent()).isSameAs(this.child);
	}

	@Test
	void testCreateChild_isUnique()
	{
		Verify.that(this.child::createChild).suppliesUniqueValues();
	}

	static class Son extends Child<Son>
	{
		Son()
		{
			
		}

		Son(Son parent)
		{
			super(parent);
		}
	}

}