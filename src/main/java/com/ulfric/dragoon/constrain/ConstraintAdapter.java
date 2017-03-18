package com.ulfric.dragoon.constrain;

import java.lang.reflect.Field;

public interface ConstraintAdapter<T> {

	void check(Field field, T object) throws ConstraintException;

	Class<T> adaptionType();

	String errorMessage();

}
