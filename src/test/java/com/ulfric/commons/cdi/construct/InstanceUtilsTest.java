package com.ulfric.commons.cdi.construct;

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
	public void test_getInstance_illegalArgument()
	{
		Verify.that(() -> InstanceUtils.getInstance(NoConstant.class)).doesThrow(IllegalArgumentException.class);
		Verify.that(() -> InstanceUtils.getInstance(OneConstant.class)).runsWithoutExceptions();
		Verify.that(() -> InstanceUtils.getInstance(OneConstant.class)).valueIsEqualTo(OneConstant.A);
	}

	@Test
	public void test_getInstance_invalidConstructor()
	{
		Verify.that(() -> InstanceUtils.getInstance(OneArg.class)).doesThrow(RuntimeException.class);
	}

	public enum NoConstant
	{
		;
	}

	public enum OneConstant
	{
		A;
	}

	public static class OneArg
	{
		public OneArg(Object object)
		{

		}
	}

}
