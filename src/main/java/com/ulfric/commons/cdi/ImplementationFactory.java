package com.ulfric.commons.cdi;

interface ImplementationFactory {

	Class<?> createImplementationClass(Class<?> parent);

}