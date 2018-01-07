package com.ulfric.dragoon.qualifier;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldQualifier extends DelegatingQualifier<Field> {

	public static String getQualifiedName(Field field) {
		return field.getDeclaringClass().getSimpleName() + '.' + field.getName();
	}

	public FieldQualifier(Field field) {
		super(field);
	}

	@Override
	public String getSimpleName() {
		String name = super.getSimpleName();
		return name == null ? delegate.getName() : name;
	}

	@Override
	public Type getType() {
		return delegate.getGenericType();
	}

	@Override
	public Type getEnclosingType() {
		return delegate.getDeclaringClass();
	}

}
