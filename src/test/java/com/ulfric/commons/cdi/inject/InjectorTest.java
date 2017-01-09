package com.ulfric.commons.cdi.inject;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.construct.BeanFactory;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class InjectorTest {

	private final BeanFactory factory = BeanFactory.newInstance();
	private final Injector injector = Injector.newInstance(this.factory);

	@Test
	public void test_injectState_throwsForNull()
	{
		Verify.that(() -> this.injector.injectState(null)).doesThrow(NullPointerException.class);
	}

	@Test
	public void test_injectState_injectsClass()
	{
		this.factory.request(InjectedClass.class);

		DependentClass dependentClass = (DependentClass) this.factory.request(DependentClass.class);

		this.injector.injectState(DependentClass.class);

		Verify.that(dependentClass.injectedClass).isNotNull();
	}

	@Test
	public void test_injectState_skipsPrimitive()
	{
		this.factory.request(InjectedClass.class);

		PrimitiveInjected primitiveInjected = (PrimitiveInjected) this.factory.request(PrimitiveInjected.class);

		this.injector.injectState(primitiveInjected);

		Verify.that(primitiveInjected.x).isZero();
	}

	public static class InjectedClass
	{

		public void foo()
		{

		}

	}

	public static class DependentClass
	{

		@Inject
		public InjectedClass injectedClass;

	}

	public static class PrimitiveInjected
	{

		@Inject
		public int x;

	}

}
