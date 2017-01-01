package com.ulfric.commons.cdi.construct.scope;

import java.lang.annotation.Annotation;

@SuppressWarnings("serial")
public class ScopeNotPresentException extends RuntimeException {

	public ScopeNotPresentException(Class<? extends Annotation> requested)
	{
		super("Requested scope: " + requested);
	}

}