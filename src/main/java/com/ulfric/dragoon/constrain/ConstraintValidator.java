package com.ulfric.dragoon.constrain;

public interface ConstraintValidator<T> {

	void check(T object);

	Class<T> validationType();

}
