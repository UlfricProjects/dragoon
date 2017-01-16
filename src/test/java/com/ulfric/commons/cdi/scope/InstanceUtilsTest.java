package com.ulfric.commons.cdi.scope;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.testing.Util;
import com.ulfric.testing.UtilTestBase;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
@Util(InstanceUtils.class)
public class InstanceUtilsTest extends UtilTestBase {

	@Test
	void testCreateInstanceOrNull_valid_nonnull()
	{
		Verify.that(InstanceUtils.createOrNull(Object.class)).isNotNull();
	}

	@Test
	void testCreateInstanceOrNull_interface_null()
	{
		Verify.that(InstanceUtils.createOrNull(Hello.class)).isNull();
	}

	private interface Hello
	{
		
	}

}