package com.ulfric.dragoon.extension.intercept.asynchronous;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

import com.ulfric.dragoon.extension.intercept.Intercept;

@Retention(RUNTIME)
@Target(METHOD)
@Intercept
public @interface Asynchronous {

	Class<? extends Supplier<? extends ForkJoinPool>> forkJoinPool() default CommonForkJoinPoolSupplier.class;

}
