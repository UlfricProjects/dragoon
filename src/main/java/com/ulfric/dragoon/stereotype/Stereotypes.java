package com.ulfric.dragoon.stereotype;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public enum Stereotypes {

	;

	private static final Set<Class<? extends Annotation>> IGNORED = new HashSet<>(Arrays.asList(
			Stereotype.class, Retention.class, Target.class
	));

	public static List<Annotation> of(Class<?> clazz)
	{
		return Stereotypes.fromAnnotations(clazz.getDeclaredAnnotations());
	}

	public static List<Annotation> of(Method method)
	{
		return Stereotypes.fromAnnotations(method.getDeclaredAnnotations());
	}

	public static List<Annotation> of(Field field)
	{
		return Stereotypes.fromAnnotations(field.getDeclaredAnnotations());
	}

	public static List<Annotation> of(Constructor<?> constructor)
	{
		return Stereotypes.fromAnnotations(constructor.getDeclaredAnnotations());
	}

	private static List<Annotation> fromAnnotations(Annotation[] annotations)
	{
		List<Annotation> stereotypes = new ArrayList<>();

		for (Annotation annotation : annotations)
		{
			if (Stereotypes.isStereotype(annotation))
			{
				Stereotypes.addStereotypesToList(stereotypes, annotation);
			}
			else
			{
				stereotypes.add(annotation);
			}
		}

		return stereotypes;
	}

	private static boolean isStereotype(Annotation annotation)
	{
		return annotation.annotationType().isAnnotationPresent(Stereotype.class);
	}

	private static void addStereotypesToList(List<Annotation> stereotypes, Annotation stereotype)
	{
		Stereotypes.streamAnnotations(stereotype.annotationType())
				.filter(Stereotypes::isNotIgnored)
				.forEach(stereotypes::add);
	}

	private static Stream<Annotation> streamAnnotations(Class<?> clazz)
	{
		return Stream.of(clazz.getDeclaredAnnotations());
	}

	private static boolean isNotIgnored(Annotation annotation)
	{
		return !Stereotypes.IGNORED.contains(annotation.annotationType());
	}

}
