package com.ulfric.dragoon.qualifier;

import java.lang.reflect.Field;

public class FieldQualifier extends DelegatingQualifier<Field> {

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

}
