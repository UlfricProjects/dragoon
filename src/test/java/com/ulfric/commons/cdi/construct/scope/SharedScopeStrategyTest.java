package com.ulfric.commons.cdi.construct.scope;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.construct.BeanFactory;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.inject.Injector;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class SharedScopeStrategyTest {

	private final BeanFactory factory = BeanFactory.newInstance();
	private final Injector injector = Injector.newInstance(this.factory);

	@Test
	public void test_getInstance_injectsState()
	{
		SharedScopeStrategy strategy = new SharedScopeStrategy();

		this.factory.request(FooInjected.class);

		FooSingleton singleton = strategy.getInstance(FooSingleton.class, null, this.injector);

		Verify.that(singleton).isNotNull();
		Verify.that(singleton.injected).isNotNull();
	}
	public static class FooInjected
	{

	}

	public enum FooSingleton
	{

		INSTANCE;

		@Inject
		public FooInjected injected;

	}

}
