package com.ulfric.dragoon.stereotype;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import com.ulfric.dragoon.UtilityTest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class StereotypesTest extends UtilityTest {

	public StereotypesTest() {
		super(Stereotypes.class);
	}

	@Test
	void testGetStereotypesWithoutExtraDirectlyPresent() {
		Truth.assertThat(Stereotypes.getStereotypes(UseThis.class, Good.class)).hasSize(1);
	}

	@Test
	void testIsAnnotatedWithoutExtraDirectlyPresent() {
		Truth.assertThat(Stereotypes.isAnnotated(UseThis.class, Good.class)).isTrue();
	}

	@Test
	void testIsAnnotatedWithoutExtraPresent() {
		Truth.assertThat(Stereotypes.isAnnotated(OrThis.class, Good.class)).isFalse();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface Good {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Stereotype
	@Good
	@interface One {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@One
	@interface UseThis {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Stereotype
	@interface Two {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Two
	@interface OrThis {
	}

}
