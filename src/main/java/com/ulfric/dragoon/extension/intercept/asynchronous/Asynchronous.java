package com.ulfric.dragoon.extension.intercept.asynchronous;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import com.ulfric.dragoon.extension.intercept.Intercept;

@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE, TYPE, FIELD })
@Intercept
public @interface Asynchronous {

	Class<? extends Supplier<? extends ExecutorService>> value() default CommonForkJoinPoolSupplier.class;

}
