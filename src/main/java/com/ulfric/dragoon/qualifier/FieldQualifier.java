package com.ulfric.dragoon.qualifier;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldQualifier extends DelegatingQualifier<Field> implements GenericQualifier {

	public FieldQualifier(Field field) {
		super(field);
	}

	@Override
	public String getName() {
		String name = super.getName();
		return name == null ? delegate.getName() : name;
	}

	@Override
	public Class<?> getType() {
		return delegate.getType();
	}

	@Override
	public Type getGenericType() {
		return delegate.getGenericType();
	}

}
