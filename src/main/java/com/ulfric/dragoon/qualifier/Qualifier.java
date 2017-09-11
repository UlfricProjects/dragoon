package com.ulfric.dragoon.qualifier;

import java.lang.reflect.AnnotatedElement;

public interface Qualifier extends AnnotatedElement {

	String getName();

	Class<?> getType();

}