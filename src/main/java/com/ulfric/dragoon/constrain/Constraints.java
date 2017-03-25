package com.ulfric.dragoon.constrain;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.google.gson.internal.Primitives;
import com.ulfric.commons.exception.Try;
import com.ulfric.dragoon.construct.InstanceUtils;

public enum Constraints {

	;

	private static final Map<Class<? extends ConstraintValidator<?>>, ConstraintValidator<?>> VALIDATORS =
			new IdentityHashMap<>();

	public static void validate(Object object)
	{
		FieldUtils.getAllFieldsList(object.getClass())
				.stream()
				.filter(Constraints::isConstrainable)
				.peek(Constraints::accessibilify)
				.forEach(field -> Constraints.validate(object, field));
	}

	private static void validate(Object object, Field field)
	{
		List<Constraint> constraints = Constraints.getConstrainingAnnotations(field);

		for (Constraint constraint : constraints)
		{
			@SuppressWarnings("unchecked")
			ConstraintValidator<Object> validator = (ConstraintValidator<Object>)
					Constraints.VALIDATORS.computeIfAbsent(constraint.validator(), Constraints::createValidator);

			Constraints.ensureFieldMatchesValidator(field, validator);

			validator.check(field, Try.to(() -> field.get(object)));
		}
	}

	private static void ensureFieldMatchesValidator(Field field, ConstraintValidator<?> validator)
	{
		if (!validator.validationType().isAssignableFrom(Primitives.wrap(field.getType())))
		{
			throw new ConstraintTypeMismatchException(
					"Validator type [" + validator.validationType().getName() +
							"] does not match field type [" + field.getType().getName() + "]"
			);
		}
	}

	private static ConstraintValidator<?> createValidator(Class<? extends ConstraintValidator<?>> clazz)
	{
		return InstanceUtils.createOrNull(clazz);
	}

	private static boolean isConstrainable(Field field)
	{
		int modifers = field.getModifiers();
		boolean annotationPresent = !Constraints.getConstrainingAnnotations(field).isEmpty();

		return !Modifier.isStatic(modifers) && annotationPresent;
	}

	private static void accessibilify(Field field)
	{
		field.setAccessible(true);
	}

	private static List<Constraint> getConstrainingAnnotations(Field field)
	{
		return Stream.of(field.getDeclaredAnnotations())
				.filter(Constraints::isConstraint)
				.map(annotation -> annotation.annotationType().getAnnotation(Constraint.class))
				.collect(Collectors.toList());
	}

	private static boolean isConstraint(Annotation annotation)
	{
		return annotation.annotationType().isAnnotationPresent(Constraint.class);
	}

}
