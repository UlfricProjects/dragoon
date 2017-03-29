package com.ulfric.dragoon.constrain;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.internal.Primitives;
import com.ulfric.commons.exception.Try;

class ConstraintProfile<T> {

	private final Class<T> validationCandidate;
	private final Map<Field, List<ConstraintValidator<?>>> validators;

	ConstraintProfile(Class<T> validationCandidate)
	{
		this.validationCandidate = validationCandidate;
		this.validators = Collections.unmodifiableMap(this.getValidators());
	}

	void check(T instance)
	{
		this.validators.forEach((field, validators) ->
		{
			Object value = Try.to(() -> field.get(instance));

			validators.forEach(validator ->
			{
				@SuppressWarnings("unchecked")
				ConstraintValidator<Object> objectValidator =
						(ConstraintValidator<Object>) validator;

				objectValidator.check(value);
			});
		});
	}

	private Map<Field, List<ConstraintValidator<?>>> getValidators()
	{
		Map<Field, List<ConstraintValidator<?>>> validators = new HashMap<>();

		for (Field field : this.validationCandidate.getDeclaredFields())
		{
			if (!this.isConstrainable(field))
			{
				continue;
			}

			List<Annotation> annotations = this.getConstraints(field);

			if (annotations.isEmpty())
			{
				continue;
			}

			field.setAccessible(true);

			List<ConstraintValidator<?>> constraints =
					annotations
							.stream()
							.map(Constraints::getConstraint)
							.peek(validator -> this.ensureFieldMatchesValidator(field, validator))
							.collect(Collectors.toList());

			validators.put(field, constraints);
		}

		return validators;
	}

	private void ensureFieldMatchesValidator(Field field, ConstraintValidator<?> validator)
	{
		if (!validator.validationType().isAssignableFrom(this.getValidatableType(field)))
		{
			throw new ConstraintTypeMismatchException(
				"Validator type [" + validator.validationType().getName() +
				"] does not match field type [" + field.getType().getName() + "]"
			);
		}
	}

	private Class<?> getValidatableType(Field field)
	{
		return Primitives.wrap(field.getType());
	}

	private boolean isConstrainable(Field field)
	{
		return !Modifier.isStatic(field.getModifiers());
	}


	private List<Annotation> getConstraints(Field field)
	{
		return Stream.of(field.getAnnotations())
				.filter(this::isConstraint)
				.collect(Collectors.toList());
	}

	private boolean isConstraint(Annotation annotation)
	{
		return annotation.annotationType().isAnnotationPresent(Constraint.class);
	}

}
