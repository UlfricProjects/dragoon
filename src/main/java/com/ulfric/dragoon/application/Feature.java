package com.ulfric.dragoon.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class Feature implements Function<Object, Application> {

	private static final List<Feature> WRAPPERS = new ArrayList<>();

	public static void register(Feature feature) {
		Objects.requireNonNull(feature, "feature");

		WRAPPERS.add(feature);
	}

	public static void disable(Feature feature) {
		Objects.requireNonNull(feature, "feature");

		WRAPPERS.remove(feature);
	}

	public static Application wrap(Object raw) {
		for (Feature wrapper : WRAPPERS) {
			Application wrapped = wrapper.apply(raw);
			if (wrapped != null) {
				return wrapped;
			}
		}

		return raw instanceof Application ? (Application) raw : null;
	}

}