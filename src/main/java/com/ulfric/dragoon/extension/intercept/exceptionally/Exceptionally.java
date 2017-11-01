package com.ulfric.dragoon.extension.intercept.exceptionally;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Consumer;

import com.ulfric.dragoon.extension.intercept.Intercept;

@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE })
@Intercept
public @interface Exceptionally {

	Class<? extends Consumer<Throwable>> value();

}
