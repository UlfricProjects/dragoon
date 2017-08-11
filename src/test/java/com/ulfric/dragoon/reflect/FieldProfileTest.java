package com.ulfric.dragoon.reflect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.inject.Inject;

class FieldProfileTest {

	private ObjectFactory factory;
	private FieldProfile.Builder builder;

	@BeforeEach
	void setup() {
		this.factory = new ObjectFactory();
		this.builder = FieldProfile.builder().setFactory(this.factory).setFlagToSearchFor(Inject.class);
	}

	@Test
	void testDefaultFilterIsNothing() {
		InjectMe inject = new InjectMe();
		this.builder.build().accept(inject);
		Truth.assertThat(inject.object).isNotNull();
	}

	@Test
	void testDefaultFailureStrategyThrowsException() {
		InjectMe inject = new InjectMe();
		this.factory.bind(Object.class).to(NoInstances.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> this.builder.build().accept(inject));
	}

	static class InjectMe {
		@Inject
		Object object;
	}

	static class NoInstances {
		NoInstances() {
			throw new RuntimeException();
		}
	}

}
