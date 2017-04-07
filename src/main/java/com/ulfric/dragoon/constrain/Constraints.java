package com.ulfric.dragoon.constrain;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import com.ulfric.dragoon.construct.InstanceUtils;

public enum Constraints {

	;

	private static final Map<Class<?>, ConstraintProfile<?>> PROFILES = new IdentityHashMap<>();
	private static final Map<Annotation, ConstraintValidator> VALIDATORS = new HashMap<>();

	private static final Map<Class<? extends ConstraintValidator>, Annotation> REVERSE_CACHE =
			new IdentityHashMap<>();

	public static <T> void validate(T object)
	{
		Objects.requireNonNull(object);

		@SuppressWarnings("unchecked")
		ConstraintProfile<T> profile = (ConstraintProfile<T>)
				Constraints.PROFILES.computeIfAbsent(object.getClass(), ConstraintProfile::new);

		profile.check(object);
	}

	public static ConstraintValidator<?> getConstraint(Annotation annotation)
	{
		if (!annotation.annotationType().isAnnotationPresent(Constraint.class))
		{
			throw new IllegalArgumentException("Annotation is not Constraint");
		}

		return Constraints.VALIDATORS.computeIfAbsent(annotation, ignored ->
		{
			ConstraintValidator<?> validator = InstanceUtils.createOrNull(
					annotation.annotationType().getAnnotation(Constraint.class).validator()
			);

			Constraints.REVERSE_CACHE.put(validator.getClass(), annotation);

			return validator;
		});
	}

	public static Annotation getAnnotation(Class<? extends ConstraintValidator> validator)
	{
		return Constraints.REVERSE_CACHE.get(validator);
	}

}
