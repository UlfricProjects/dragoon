package com.ulfric.dragoon.container;

import java.util.function.BiFunction;

public interface FeatureWrapper<T> extends BiFunction<Feature, T, Feature> {

}