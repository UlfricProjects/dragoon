package com.ulfric.commons.cdi.container;

import java.util.function.Function;

public interface ComponentWrapper<T> extends Function<T, Component> {

}