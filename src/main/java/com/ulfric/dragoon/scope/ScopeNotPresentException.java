package com.ulfric.dragoon.scope;

@SuppressWarnings("serial")
public class ScopeNotPresentException extends RuntimeException {

	public ScopeNotPresentException(Class<?> scope)
	{
		super("Tried to use scope: " + scope);
	}

}