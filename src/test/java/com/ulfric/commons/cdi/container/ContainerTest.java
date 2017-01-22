package com.ulfric.commons.cdi.container;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.exception.Try;
import com.ulfric.commons.logging.Logger;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ContainerTest {

	private final Component component = Mockito.mock(Component.class);

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
			Field field = Container.class.getDeclaredField("COMPONENT_WRAPPERS");
			field.setAccessible(true);
			Map map = (Map) field.get(null);
			map.clear();
		});
	}

	@Test
	void testRegisterComponentWrapper_nullRequest()
	{
		Verify.that(() -> Container.registerComponentWrapper(null, o -> Mockito.mock(Component.class))).doesThrow(NullPointerException.class);
	}

	@Test
	void testRegisterComponentWrapper_nullWrapper()
	{
		Verify.that(() -> Container.registerComponentWrapper(Hello.class, null)).doesThrow(NullPointerException.class);
	}

	@Test
	void testRegisterComponentWrapper_validValues()
	{
		Verify.that(() -> Container.registerComponentWrapper(Hello.class, new HelloComponent())).runsWithoutExceptions();
	}

	@Test
	void testGetComponentWrapper_unregisteredRequest()
	{
		Verify.that(this.getComponentWrapper(Hello.class)).isNull();
	}

	@Test
	void testGetComponentWrapper_superclassRequest()
	{
		Container.registerComponentWrapper(Hello.class, new HelloComponent());
		Verify.that(this.getComponentWrapper(SubHello.class)).isInstanceOf(HelloComponent.class);
	}

	@Test
	void testGetComponentWrapper_exactRequest()
	{
		Container.registerComponentWrapper(Hello.class, new HelloComponent());
		Verify.that(this.getComponentWrapper(Hello.class)).isInstanceOf(HelloComponent.class);
	}

	@Test
	void testInstall_nullComponent()
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
		Verify.that(() -> this.container.install(Hello.class)).doesThrow(ComponentWrapperMissingException.class);
	}

	@Test
	void testInstall_wrappedClass()
	{
		Container.registerComponentWrapper(Hello.class, new HelloComponent());
		Verify.that(() -> this.container.install(Hello.class)).runsWithoutExceptions();
	}

	@Test
	void testLoad()
	{
		Verify.that(() -> this.container.load()).runsWithoutExceptions();
	}

	@Test
	void testLoad_alreadyLoaded()
	{
		Try.to(() ->
		{
			Field loadedField = Container.class.getDeclaredField("loaded");
			loadedField.setAccessible(true);
			loadedField.set(this.container, true);
		});

		Verify.that(() -> this.container.load()).doesThrow(IllegalStateException.class);
	}

	@Test
	void testEnable()
	{
		Verify.that(() -> this.container.enable()).runsWithoutExceptions();
	}

	@Test
	void testLoad_alreadyEnabled()
	{
		Try.to(() ->
		{
			Field loadedField = Container.class.getDeclaredField("enabled");
			loadedField.setAccessible(true);
			loadedField.set(this.container, true);
		});

		Verify.that(() -> this.container.enable()).doesThrow(IllegalStateException.class);
	}

	@Test
	void testDisable()
	{
		Try.to(() ->
		{
			Field loadedField = Container.class.getDeclaredField("enabled");
			loadedField.setAccessible(true);
			loadedField.set(this.container, true);
		});

		Verify.that(() -> this.container.disable()).runsWithoutExceptions();
	}

	@Test
	void testLoad_alreadyDisabled()
	{
		Verify.that(() -> this.container.disable()).doesThrow(IllegalStateException.class);
	}

	@Test
	void testLoadIfNotLoaded_alreadyLoaded()
	{
		Try.to(() ->
		{
			Field loadedField = Container.class.getDeclaredField("loaded");
			loadedField.setAccessible(true);
			loadedField.set(this.container, true);

			Method method = Container.class.getDeclaredMethod("loadIfNotLoaded");
			method.setAccessible(true);

			Verify.that(() -> method.invoke(this.container)).runsWithoutExceptions();
			Verify.that((boolean) loadedField.get(this.container)).isTrue();
		});
	}

	private <T> ComponentWrapper<T> getComponentWrapper(Class<T> request)
	{
		return Try.to(() -> {
			Method method = MethodUtils.getMatchingMethod(Container.class, "getComponentWrapper", Class.class);
			method.setAccessible(true);
			return (ComponentWrapper<T>) method.invoke(null, request);
		});
	}

	public static class Hello
	{

	}

	public static class SubHello extends Hello
	{

	}

	public final class HelloComponent implements ComponentWrapper<Hello>
	{

		@Override
		public Component apply(Hello hello)
		{
			return ContainerTest.this.component;
		}

	}

	public static class FooContainer extends Container
	{

	}

	public static class NullLogger implements Logger
	{

		@Override
		public void info(String s)
		{

		}

		@Override
		public void error(String s)
		{

		}

	}

}