package com.ulfric.dragoon.extension.intercept.asynchronous;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.ulfric.dragoon.extension.intercept.Intercept;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@Retention(RUNTIME)
@Target(METHOD)
@Intercept
public @interface Asynchronous {

	Class<? extends Supplier<? extends ExecutorService>> executor() default CommonForkJoinPoolSupplier.class;

}
