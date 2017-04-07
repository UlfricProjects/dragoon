package com.ulfric.dragoon.stereotype;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.exception.CheckedSupplier;
import com.ulfric.commons.exception.Try;
import com.ulfric.testing.Util;
import com.ulfric.testing.UtilTestBase;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
@Util(Stereotypes.class)
public class StereotypesTest extends UtilTestBase {

	@Test
	void testOf_classIsStereotyped()
	{
		List<Class<? extends Annotation>> annotations =
				Stereotypes.of(Stereotyped.class)
						.stream()
						.map(Annotation::annotationType)
						.collect(Collectors.toList());

		this.check(annotations);
	}

	@Test
	void testOf_methodIsStereotyped()
	{
		Method method = Try.to(() -> Stereotyped.class.getDeclaredMethod("method"));

		List<Class<? extends Annotation>> annotations =
				Stereotypes.of(method)
						.stream()
						.map(Annotation::annotationType)
						.collect(Collectors.toList());

		this.check(annotations);
	}

	@Test
	void testOf_constructorIsStereotyped()
	{
		Constructor<Stereotyped> constructor =
				Try.to((CheckedSupplier<Constructor<Stereotyped>>) Stereotyped.class::getDeclaredConstructor);

		List<Class<? extends Annotation>> annotations =
				Stereotypes.of(constructor)
						.stream()
						.map(Annotation::annotationType)
						.collect(Collectors.toList());

		this.check(annotations);
	}

	@Test
	void testOf_fieldIsStereotyped()
	{
		Field field = Try.to(() -> Stereotyped.class.getDeclaredField("field"));

		List<Class<? extends Annotation>> annotations =
				Stereotypes.of(field)
						.stream()
						.map(Annotation::annotationType)
						.collect(Collectors.toList());

		this.check(annotations);
	}

	private void check(List<Class<? extends Annotation>> annotations)
	{
		Verify.that(annotations).contains(Bar.class);
		Verify.that(annotations).contains(Foo.class);
		Verify.that(annotations).doesNotContain(Stereotype.class);
		Verify.that(annotations).doesNotContain(Stereotyped.class);
	}

	@Stereo
	@Foo
	public static class Stereotyped
	{

		@Stereo
		@Foo
		private String field;

		@Stereo
		@Foo
		private Stereotyped()
		{

		}

		@Stereo
		@Foo
		void method()
		{

		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
	@Stereotype
	@Bar
	public @interface Stereo
	{

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface Bar
	{

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
	public @interface Foo
	{

	}

}
