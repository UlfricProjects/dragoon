package com.ulfric.dragoon.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class Feature implements Function<Object, Application> { // TODO make this state tied to ObjectFactory?

	private static final List<Feature> WRAPPERS = new ArrayList<>();

	public static void register(Feature feature) {
		Objects.requireNonNull(feature, "feature");

		WRAPPERS.add(feature);
	}

	public static void unregister(Feature feature) {
		Objects.requireNonNull(feature, "feature");

		WRAPPERS.remove(feature);
	}

	public static Application wrap(Object raw) {
		List<Application> applications = new ArrayList<>();

		for (Feature wrapper : WRAPPERS) {
			Application wrapped = wrapper.apply(raw);
			if (wrapped != null) {
				applications.add(wrapped);
			}
		}

		if (raw instanceof Application) {
			applications.add((Application) raw);
		}

		if (applications.isEmpty()) {
			return null;
		}

		return new AggregateApplication(applications);
	}

}
