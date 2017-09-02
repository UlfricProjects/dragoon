package com.ulfric.dragoon;

import org.junit.jupiter.api.BeforeEach;

public abstract class DragoonTestSuite {

	protected ObjectFactory factory;

	@BeforeEach
	final void setupObjectFactory() {
		factory = ObjectFactoryTestHelper.newObjectFactory();
	}

}
