package com.ulfric.dragoon.constrain;

import java.util.IdentityHashMap;
import java.util.Map;

public enum Constraints {

	;

	private static final Map<Class<?>, ConstraintProfile<?>> PROFILES = new IdentityHashMap<>();

	public static <T> void validate(T object)
	{
		@SuppressWarnings("unchecked")
		ConstraintProfile<T> profile = (ConstraintProfile<T>)
				Constraints.PROFILES.computeIfAbsent(
						object.getClass(),
						ignored -> new ConstraintProfile(object.getClass())
				);

		profile.check(object);
	}

}
