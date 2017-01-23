package com.ulfric.commons.cdi.container;

import java.util.function.BiFunction;

public interface ComponentWrapper<T> extends BiFunction<Component, T, Component> {

}