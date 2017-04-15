package com.ulfric.dragoon.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import com.ulfric.dragoon.intercept.Intercept;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Intercept
public @interface Cache {

	@SuppressWarnings("rawtypes")
	Class<? extends Map> value() default HashMap.class;

}