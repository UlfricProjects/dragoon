package com.ulfric.commons.cdi;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ChildTest {

	@Test
	void testHasParent_root()
	{
		Verify.that(new Child<>().hasParent()).isFalse();
	}

	@Test
	void testHasParent_null_isRoot()
	{
		Verify.that(new Child<>(null).hasParent()).isFalse();
	}

	@Test
	void testHasParent_child()
	{
		Verify.that(new Child<>(new Object()).hasParent()).isTrue();
	}

	@Test
	void testGetParent_root()
	{
		Verify.that(new Child<>().getParent()).isNull();
	}

	@Test
	void testGetParent_child()
	{
		Object o = new Object();
		Verify.that(new Child<>(o).getParent()).isSameAs(o);
	}

	@Test
	void testCreateChild()
	{
		Verify.that(new Child<>().createChild()).isNotNull();
	}

}