package com.ulfric.dragoon.stereotype;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Stereotypes {

	public static List<Field> getAnnotatedInstanceFields(Class<?> type, Class<? extends Annotation> annotation)
	{
		List<Field> fields = new ArrayList<>();

		for (Field field : type.getDeclaredFields())
		{
			if (Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}

			if (!Stereotypes.isAnnotated(field, annotation))
			{
				continue;
			}

			fields.add(field);
		}

		Class<?> superType = type.getSuperclass();
		if (superType != null)
		{
			fields.addAll(Stereotypes.getAnnotatedInstanceFields(superType, annotation));
		}
		return fields;
	}

	public static boolean isAnnotated(AnnotatedElement holder, Class<? extends Annotation> annotation)
	{
		for (Annotation held : holder.getAnnotations())
		{
			Class<?> heldType = held.annotationType();
			if (heldType == annotation)
			{
				return true;
			}

			if (heldType.isAnnotationPresent(Stereotype.class))
			{
				if (Stereotypes.isAnnotated(heldType, annotation))
				{
					return true;
				}
			}
		}

		return false;
	}

	public static <T extends Annotation> List<T> getAnnotations(AnnotatedElement holder, Class<T> annotation)
	{
		List<T> annotations = new ArrayList<>();

		for (Annotation held : holder.getAnnotations())
		{
			Class<?> heldType = held.annotationType();
			if (heldType == annotation)
			{
				@SuppressWarnings("unchecked")
				T casted = (T) held;
				annotations.add(casted);
				continue;
			}

			if (heldType.isAnnotationPresent(Stereotype.class))
			{
				annotations.addAll(Stereotypes.getAnnotations(heldType, annotation));
			}
		}

		return annotations;
	}

	public static List<Annotation> getStereotypes(AnnotatedElement holder, Class<? extends Annotation> stereotype)
	{
		List<Annotation> annotations = new ArrayList<>();

		for (Annotation held : holder.getAnnotations())
		{
			Class<?> heldType = held.annotationType();

			if (heldType.isAnnotationPresent(stereotype))
			{
				annotations.add(held);
			}

			if (heldType.isAnnotationPresent(Stereotype.class))
			{
				annotations.addAll(Stereotypes.getStereotypes(heldType, stereotype));
			}
		}

		return annotations;
	}

	private Stereotypes() { }

}