package com.ulfric.dragoon.construct;

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

	@Test
	void testCreateInstanceOrNull_null_null()
	{
		Verify.that(() -> InstanceUtils.createOrNull(null)).doesThrow(NullPointerException.class);
	}

	@Test
	void testCreateInstanceOrNull_enumNotEmpty_nonnull()
	{
		Verify.that(InstanceUtils.createOrNull(Greeting.class)).isSameAs(Greeting.HELLO);
	}
	
	@Test
	void testCreateInstanceOrNullArgs_enumNotEmpty_nonnull()
	{
		Verify.that(InstanceUtils.createOrNull(Greeting.class)).isSameAs(Greeting.HELLO);
	}

	@Test
	void testCreateInstanceOrNull_enumEmpty_null()
	{
		Verify.that(InstanceUtils.createOrNull(Empty.class)).isNull();
	}

	@Test
	public void testCreateInstanceOrNullArgs_valid_nonnull()
	{
	    Verify.that(InstanceUtils.createOrNull(Object.class)).isNotNull();
	}

	private interface Hello
	{
		
	}

	private enum Greeting
	{
		HELLO;
	}

	private enum Empty
	{
		
	}

}