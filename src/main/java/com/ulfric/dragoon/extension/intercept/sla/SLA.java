package com.ulfric.dragoon.extension.intercept.sla;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import com.ulfric.dragoon.extension.intercept.Intercept;

@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE })
@Intercept
public @interface SLA {

	long value();

	TimeUnit unit() default TimeUnit.MILLISECONDS;

}
