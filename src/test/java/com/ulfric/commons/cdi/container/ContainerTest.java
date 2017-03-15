package com.ulfric.commons.cdi.container;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.exception.Try;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ContainerTest {

	final Feature feature = Mockito.mock(Feature.class);

	private ObjectFactory factory;
	private Container container;

	@BeforeEach
	void init()
	{
		this.factory = ObjectFactory.newInstance();
		this.factory.bind(Logger.class).to(NullLogger.class);
		this.container = this.factory.requestExact(Container.class);

		Try.to(() ->
		{
			Field field = Container.class.getDeclaredField("FEATURE_WRAPPERS");
			field.setAccessible(true);
			Map<?, ?> map = (Map<?, ?>) field.get(null);
			map.clear();
		});
	}

	@Test
	void testRegisterFeatureWrapper_nullRequest()
	{
		Verify.that(() -> Container.registerFeatureWrapper(null, (ignore, o) -> Mockito.mock(Feature.class))).doesThrow(NullPointerException.class);
	}

	@Test
	void testRegisterFeatureWrapper_nullWrapper()
	{
		Verify.that(() -> Container.registerFeatureWrapper(Hello.class, null)).doesThrow(NullPointerException.class);
	}

	@Test
	void testRegisterFeatureWrapper_validValues()
	{
		Verify.that(() -> Container.registerFeatureWrapper(Hello.class, new HelloFeature())).runsWithoutExceptions();
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

	@Test
	void testInstall_nullFeature()
	{
		Verify.that(() -> this.container.install(null)).doesThrow(NullPointerException.class);
	}

	@Test
	void testInstall_container()
	{
		Verify.that(() -> this.container.install(FooContainer.class)).runsWithoutExceptions();
	}

	@Test
	void testInstall_arbitraryClass()
	{
		Verify.that(() -> this.container.install(Hello.class)).doesThrow(FeatureWrapperMissingException.class);
	}

	@Test
	void testInstall_wrappedClass()
	{
		Container.registerFeatureWrapper(Hello.class, new HelloFeature());
		Verify.that(() -> this.container.install(Hello.class)).runsWithoutExceptions();
	}

	@Test
	void testLoad_alreadyLoaded()
	{
		this.container.load();
		Verify.that(() -> this.container.load()).doesThrow(IllegalStateException.class);
	}

	@Test
	void testLoad_alreadyEnabled()
	{
		this.container.enable();
		Verify.that(() -> this.container.enable()).doesThrow(IllegalStateException.class);
	}

	@Test
	void testDisable()
	{
		this.container.enable();
		Verify.that(() -> this.container.disable()).runsWithoutExceptions();
	}

	@Test
	void testLoad_alreadyDisabled()
	{
		Verify.that(() -> this.container.disable()).doesThrow(IllegalStateException.class);
	}

	private <T> FeatureWrapper<T> getFeatureWrapper(Class<T> request)
	{
		return Try.to(() -> {
			Method method = MethodUtils.getMatchingMethod(Container.class, "getFeatureWrapper", Class.class);
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
			return ContainerTest.this.feature;
		}

	}

	public static class FooContainer extends Container
	{

	}

}
