package com.ulfric.commons.cdi.construct.scope;

import java.lang.annotation.Annotation;

import com.ulfric.commons.cdi.inject.Injector;

public interface ScopeStrategy<S extends Annotation> {

	<T> T getInstance(Class<T> request, S scope, Injector injector);

}