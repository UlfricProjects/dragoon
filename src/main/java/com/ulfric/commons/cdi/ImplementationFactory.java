package com.ulfric.commons.cdi;

interface ImplementationFactory {

	<T> Class<? extends T> createImplementationClass(Class<T> parent);

}