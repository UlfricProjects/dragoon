package com.ulfric.dragoon.reflect;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.UtilityTest;

@RunWith(JUnitPlatform.class)
class InstancesTest extends UtilityTest {

	public InstancesTest() {
		super(Instances.class);
	}

	@Test
	void testNewInstanceWhenPassedInvalidParameter() {
		Truth.assertThat(Instances.newInstance(NewInstance.class, new Object())).isNull();
	}

	static class NewInstance {
		Integer object;

		NewInstance(Integer object) {
			this.object = object;
		}

		NewInstance(Integer object, @SuppressWarnings("unused") Object two) {
			this.object = object;
		}
	}

}
