package com.ulfric.dragoon.qualifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public enum EmptyQualifier implements Qualifier {

	INSTANCE;

	private final Annotation[] annotations = new Annotation[0];

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> arg0) {
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return annotations;
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return annotations;
	}

	@Override
	public String toString() {
		return "EmptyQualifier";
	}

	@Override
	public Type getType() {
		return Object.class;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Type getEnclosingType() {
		return null;
	}

}
