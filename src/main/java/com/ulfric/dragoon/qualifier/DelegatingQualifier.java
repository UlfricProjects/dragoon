package com.ulfric.dragoon.qualifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

import com.ulfric.dragoon.naming.Name;
import com.ulfric.dragoon.stereotype.Stereotypes;

public abstract class DelegatingQualifier<A extends AnnotatedElement> implements Qualifier {

	protected final A delegate;

	public DelegatingQualifier(A delegate) {
		Objects.requireNonNull(delegate, "delegate");

		this.delegate = delegate;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return delegate.getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getAnnotations() {
		return delegate.getAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return delegate.getDeclaredAnnotations();
	}

	@Override
	public String getSimpleName() {
		Name name = Stereotypes.getFirst(this, Name.class);
		if (name == null) {
			return null;
		}
		return name.value();
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), getSimpleName());
	}

}
