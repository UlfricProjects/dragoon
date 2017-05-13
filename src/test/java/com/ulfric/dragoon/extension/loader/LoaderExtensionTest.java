package com.ulfric.dragoon.extension.loader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Container;

@RunWith(JUnitPlatform.class)
class LoaderExtensionTest {

	private ObjectFactory factory;

	@BeforeEach
	void setup()
	{
		this.factory = new ObjectFactory();
	}

	@AfterEach
	void teardown()
	{
		App.last = null;
	}

	@Test
	void test()
	{
		Container container = this.factory.request(Container.class);
		System.out.println(container.getClass());
		/*this.factory.request(Container.class).install(AppContainer.class);
		Container container = this.factory.request(AppContainer.class);
		container.start();
		container.shutdown();

		Truth.assertThat(App.last.container).isSameAs(container);*/
	}

	static class InContainer
	{
		
	}

}