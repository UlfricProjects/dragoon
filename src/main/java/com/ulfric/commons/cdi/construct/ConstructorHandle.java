package com.ulfric.commons.cdi.construct;

interface ConstructorHandle {

	Object invoke();
	
	// TODO: 2/2/2017 Possibly merge with #invoke() 
	Object invoke(Object... args);

}