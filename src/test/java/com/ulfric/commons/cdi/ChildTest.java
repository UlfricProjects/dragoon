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

}