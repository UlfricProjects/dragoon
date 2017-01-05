package com.ulfric.commons.cdi.intercept.async;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ulfric.commons.cdi.intercept.Intercept;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Intercept
public @interface Asynchronous {

}
