package com.ulfric.commons.cdi;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class BindingsTest {

	@Test
	void testHasParent_root()
	{
		Verify.that(new Bindings().hasParent()).isFalse();
	}

	@Test
	void testHasParent_child()
	{
		Verify.that(new Bindings(new Bindings()).hasParent()).isTrue();
	}

}