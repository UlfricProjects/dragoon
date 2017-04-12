package com.ulfric.dragoon.container;

import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ulfric.commons.exception.Try;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.TestObjectFactory;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class FeatureStateControllerTest {

	@Mock
	private Feature feature;

	private ObjectFactory factory;
	private Container container;
	private FeatureStateController states;

	@BeforeEach
	void init()
	{
		MockitoAnnotations.initMocks(this);
		this.factory = TestObjectFactory.newInstance();
		this.container = this.factory.requestExact(Container.class);
		this.states = FeatureStateController.newInstance(this.factory, this.container);
		TestContainer.clearFeatureWrappers();
	}

	@Test
	void testInstall_nullFeature()
	{
		Verify.that(() -> this.states.install(null)).doesThrow(NullPointerException.class);
	}

	@Test
	void testInstall_container()
	{
		this.container.enable();
		Verify.that(() -> this.states.install(TestContainer.class)).runsWithoutExceptions();
	}

	@Test
	void testInstall_containerLazily()
	{
		Verify.that(() -> this.states.install(TestContainer.class)).runsWithoutExceptions();
	}

	@Test
	void testInstall_arbitraryClass()
	{
		this.container.enable();
		Verify.that(() -> this.states.install(Hello.class)).doesThrow(FeatureWrapperMissingException.class);
	}

	@Test
	void testInstall_wrappedClass()
	{
		this.container.enable();
		Container.registerFeatureWrapper(Hello.class, new HelloFeature());
		Verify.that(() -> this.states.install(Hello.class)).runsWithoutExceptions();
	}

	@Test
	void testInstall_twice()
	{
		Verify.that(() -> this.states.install(TestContainer.class)).runsWithoutExceptions();
		Verify.that(() -> this.states.install(TestContainer.class)).doesThrow(IllegalStateException.class);
	}

	@Test
	void testGetFeatureWrapper_unregisteredRequest()
	{
		Verify.that(this.getFeatureWrapper(Hello.class)).isNull();
	}

	@Test
	void testGetFeatureWrapper_superclassRequest()
	{
		Container.registerFeatureWrapper(Hello.class, new HelloFeature());
		Verify.that(this.getFeatureWrapper(SubHello.class)).isInstanceOf(HelloFeature.class);
	}

	@Test
	void testGetFeatureWrapper_exactRequest()
	{
		Container.registerFeatureWrapper(Hello.class, new HelloFeature());
		Verify.that(this.getFeatureWrapper(Hello.class)).isInstanceOf(HelloFeature.class);
	}

	private <T> FeatureWrapper<T> getFeatureWrapper(Class<T> request)
	{
		return Try.to(() -> {
			Method method = MethodUtils.getMatchingMethod(FeatureStateController.class, "getFeatureWrapper", Class.class);
			method.setAccessible(true);
			@SuppressWarnings("unchecked")
			FeatureWrapper<T> casted = (FeatureWrapper<T>) method.invoke(null, request);
			return casted;
		});
	}

	public static class Hello
	{

	}

	public static class SubHello extends Hello
	{

	}

	public final class HelloFeature implements FeatureWrapper<Hello>
	{
		@Override
		public Feature apply(Feature parent, Hello hello)
		{
			return FeatureStateControllerTest.this.feature;
		}
	}

}