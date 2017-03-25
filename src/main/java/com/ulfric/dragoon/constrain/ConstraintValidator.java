package com.ulfric.dragoon.constrain;

import java.lang.reflect.Field;

public interface ConstraintValidator<T> {

	void check(Field field, T object);

	Class<T> validationType();

	String errorMessage();

}
