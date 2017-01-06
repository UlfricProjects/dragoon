package com.ulfric.commons.cdi.intercept.random;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.construct.BeanFactory;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ChanceToRunTest {

	private final BeanFactory factory = BeanFactory.newInstance();
	private final ChanceTest chanceTest = (ChanceTest) this.factory.request(ChanceTest.class);

	@Test
	public void test_chanceToRun_noChance()
	{
		Verify.that(this.chanceTest::noChance).runsWithoutExceptions();
	}

	@Test
	public void test_chanceToRun_definiteChance()
	{
		Verify.that(this.chanceTest::definiteChance).valueIsNotNull();
	}

	public static class ChanceTest
	{

		@ChanceToRun(value = 0.0)
		public void noChance()
		{
			throw new RuntimeException();
		}

		@ChanceToRun(value = 1.0)
		public Object definiteChance()
		{
			return new Object();
		}

	}

}
