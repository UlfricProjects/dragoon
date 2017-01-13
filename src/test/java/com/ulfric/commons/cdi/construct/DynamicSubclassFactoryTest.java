package com.ulfric.commons.cdi.construct;

import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.construct.BeanFactoryTest.FooAbstractClass;
import com.ulfric.commons.cdi.construct.BeanFactoryTest.FooClass;
import com.ulfric.commons.cdi.construct.BeanFactoryTest.FooEnum;
import com.ulfric.commons.cdi.construct.BeanFactoryTest.FooFinalClass;
import com.ulfric.commons.cdi.construct.BeanFactoryTest.FooInterface;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class DynamicSubclassFactoryTest {

	@Test
	public void test_canBeIntercepted_all()
	{
		Method method = this.getCanBeIntercepted();

		Verify.that(() -> method.invoke(this.getFactory(FooClass.class))).valueIsEqualTo(true);
		Verify.that(() -> method.invoke(this.getFactory(FooInterface.class))).valueIsEqualTo(false);
		Verify.that(() -> method.invoke(this.getFactory(FooEnum.class))).valueIsEqualTo(false);
		Verify.that(() -> method.invoke(this.getFactory(FooAbstractClass.class))).valueIsEqualTo(false);
		Verify.that(() -> method.invoke(this.getFactory(FooFinalClass.class))).valueIsEqualTo(false);
	}

	private Method getCanBeIntercepted()
	{
		Method method = MethodUtils.getMatchingMethod(
				DynamicSubclassFactory.class, "canBeIntercepted");

		method.setAccessible(true);

		return method;
	}

	private <T> DynamicSubclassFactory<T> getFactory(Class<T> parent)
	{
		return new DynamicSubclassFactory<>(null, parent);
	}

}