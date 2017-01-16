package com.ulfric.commons.cdi.scope;

import com.ulfric.commons.exception.Try;

public enum InstanceUtils {

	;

	public static <T> T createOrNull(Class<T> clazz)
	{
		return Try.to(clazz::newInstance);
	}

}