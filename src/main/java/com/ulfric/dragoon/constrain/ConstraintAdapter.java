package com.ulfric.dragoon.constrain;

public interface ConstraintAdapter<T> {

	void check(T object) throws ConstraintException;

	Class<T> adaptionType();

}
