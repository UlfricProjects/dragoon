package com.ulfric.dragoon.constrain;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.internal.Primitives;
import com.ulfric.commons.exception.Try;
import com.ulfric.dragoon.construct.InstanceUtils;

public class ConstraintProfile<T> {

	private final Class<T> clazz;
	private final Map<Field, List<ConstraintValidator<?>>> validators = new HashMap<>();

	public ConstraintProfile(Class<T> clazz)
	{
		this.clazz = clazz;
		this.loadValidators();
	}

	public void check(T instance)
	{
		this.validators.keySet().forEach(field ->
		{
			Object value = Try.to(() -> field.get(instance));

			this.validators.get(field).forEach(validator ->
			{
				@SuppressWarnings("unchecked")
				ConstraintValidator<Object> objectValidator =
						(ConstraintValidator<Object>) validator;

				objectValidator.check(field, value);
			});
		});
	}

	private void loadValidators()
	{
		for (Field field : this.clazz.getDeclaredFields())
		{
			if (!this.isConstrainable(field))
			{
				continue;
			}

			List<Constraint> constraints = this.getConstraints(field);

			if (constraints.isEmpty())
			{
				continue;
			}

			field.setAccessible(true);

			List<ConstraintValidator<?>> validators =
					constraints
							.stream()
							.map(Constraint::validator)
							.map(InstanceUtils::createOrNull)
							.peek(validator -> this.ensureFieldMatchesValidator(field, validator))
							.collect(Collectors.toList());

			this.validators.put(field, validators);
		}
	}

	private void ensureFieldMatchesValidator(Field field, ConstraintValidator<?> validator)
	{
		if (!validator.validationType().isAssignableFrom(Primitives.wrap(field.getType())))
		{
			throw new ConstraintTypeMismatchException(
				"Validator type [" + validator.validationType().getName() +
				"] does not match field type [" + field.getType().getName() + "]"
			);
		}
	}

	private boolean isConstrainable(Field field)
	{
		return !Modifier.isStatic(field.getModifiers());
	}


	private List<Constraint> getConstraints(Field field)
	{
		return Stream.of(field.getDeclaredAnnotations())
				.filter(this::isConstraint)
				.map(annotation -> annotation.annotationType().getAnnotation(Constraint.class))
				.collect(Collectors.toList());
	}

	private boolean isConstraint(Annotation annotation)
	{
		return annotation.annotationType().isAnnotationPresent(Constraint.class);
	}

}
