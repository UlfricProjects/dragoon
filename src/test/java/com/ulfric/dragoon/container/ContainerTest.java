package com.ulfric.dragoon.container;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.ulfric.commons.exception.Try;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.TestObjectFactory;
import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ContainerTest {

	final Feature feature = Mockito.mock(Feature.class);

	private ObjectFactory factory;
	private Container container;

	@BeforeEach
	void init()
	{
		this.factory = TestObjectFactory.newInstance();
		this.container = this.factory.requestExact(Container.class);

		Try.to(() ->
		{
			Field field = FeatureStateController.class.getDeclaredField("FEATURE_WRAPPERS");
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
	void testInstall_container()
	{
		Verify.that(() -> this.container.install(TestContainer.class)).runsWithoutExceptions();
	}

	@Test
	void testEnable_alreadyEnabled()
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

	@Test
	void testFactory()
	{
		this.container.bind(Object.class).to(Hello.class);
		Verify.that(this.container.request(Object.class)).isInstanceOf(Hello.class);
		Verify.that(this.container.requestExact(Hello.class)).isExactType(Hello.class);
	}

	@Test
	void testSharedWithChildren()
	{
		Container container = this.factory.requestExact(ContainerA.class);
		container.enable();
		Verify.that(FeatureA.lastContainer).isSameAs(container);
	}

	public static class ContainerA extends Container
	{
		@Initialize
		private void initialize()
		{
			this.install(FeatureA.class);
		}
	}

	public static class FeatureA extends SkeletalFeature
	{
		private static Container lastContainer;

		@Inject
		private Container container;

		@Initialize
		private void initialize()
		{
			FeatureA.lastContainer = this.container;
		}
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

}
