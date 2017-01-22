package com.ulfric.commons.cdi.container;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ContainerTest {

	private final Component component = Mockito.mock(Component.class);

	private ObjectFactory factory = ObjectFactory.newInstance();
	private Container container;

	@BeforeEach
	void init()
	{
		this.container = this.factory.requestExact(Container.class);
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

	private static class Hello
	{

	}

	private final class HelloComponent implements ComponentWrapper<Hello>
	{

		@Override
		public Component apply(Hello hello)
		{
			return ContainerTest.this.component;
		}

	}

}
