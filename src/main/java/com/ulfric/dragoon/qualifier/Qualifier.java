package com.ulfric.dragoon.qualifier;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public interface Qualifier extends AnnotatedElement {

	String getName();

	Type getType();

	Type getEnclosingType();

}
