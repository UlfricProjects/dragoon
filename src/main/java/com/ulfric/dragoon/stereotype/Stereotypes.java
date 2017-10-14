package com.ulfric.dragoon.stereotype;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Stereotypes { // TODO refactor method names, support field/method inheritance from class

	public static List<Field> getAnnotatedInstanceFields(Class<?> type, Class<? extends Annotation> annotation) {
		List<Field> fields = new ArrayList<>();

		for (Field field : type.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			if (field.isSynthetic()) {
				continue;
			}

			if (field.isEnumConstant()) {
				continue;
			}

			if (!isAnnotated(field, annotation)) {
				continue;
			}

			fields.add(field);
		}

		Class<?> superType = type.getSuperclass();
		if (containsFields(type)) {
			fields.addAll(getAnnotatedInstanceFields(superType, annotation));
		}
		return fields;
	}

	private static boolean containsFields(Class<?> type) {
		return type != null && type != Object.class;
	}

	public static boolean isAnnotated(AnnotatedElement holder, Class<? extends Annotation> annotation) {
		for (Annotation held : holder.getAnnotations()) {
			Class<?> heldType = held.annotationType();
			if (heldType == annotation) {
				return true;
			}

			if (heldType.isAnnotationPresent(Stereotype.class)) {
				if (isAnnotated(heldType, annotation)) {
					return true;
				}
			}
		}

		return false;
	}

	public static List<Annotation> getStereotypes(AnnotatedElement holder, Class<? extends Annotation> stereotype) {
		List<Annotation> annotations = new ArrayList<>();

		for (Annotation held : holder.getAnnotations()) {
			Class<?> heldType = held.annotationType();

			if (heldType.isAnnotationPresent(stereotype)) {
				annotations.add(held);
			}

			if (heldType.isAnnotationPresent(Stereotype.class)) {
				annotations.addAll(getStereotypes(heldType, stereotype));
			}
		}

		return annotations;
	}

	public static boolean isPresent(AnnotatedElement holder, Class<? extends Annotation> stereotype) {
		return getFirst(holder, stereotype) != null;
	}

	public static <T extends Annotation> T getFirst(AnnotatedElement holder, Class<T> stereotype) {
		for (Annotation held : holder.getAnnotations()) {
			Class<?> heldType = held.annotationType();

			if (heldType == stereotype) {
				return stereotype.cast(held);
			}

			T inherited = heldType.getAnnotation(stereotype);
			if (inherited == null) {
				if (heldType.isAnnotationPresent(Stereotype.class)) {
					inherited = getFirst(heldType, stereotype);
				}
			}

			if (inherited != null) {
				return inherited;
			}
		}

		return null;
	}

	public static <T extends Annotation> List<T> getAll(AnnotatedElement holder, Class<T> stereotype) {
		List<T> stereotypes = new ArrayList<>();

		for (Annotation held : holder.getAnnotations()) {
			Class<?> heldType = held.annotationType();

			if (heldType == stereotype) {
				stereotypes.add(stereotype.cast(held));
			}

			if (heldType.isAnnotationPresent(Stereotype.class)) {
				T inheritedSingle = heldType.getAnnotation(stereotype);
				if (inheritedSingle != null) {
					stereotypes.add(inheritedSingle);
					continue;
				}

				stereotypes.addAll(getAll(heldType, stereotype));
			}
		}

		return stereotypes;
	}

	private Stereotypes() {}

}
