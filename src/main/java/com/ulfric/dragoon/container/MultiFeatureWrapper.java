package com.ulfric.dragoon.container;

import java.util.List;
import java.util.stream.Collectors;

public class MultiFeatureWrapper<T> implements FeatureWrapper<T> {

	private final List<FeatureWrapper<T>> contained;

	public MultiFeatureWrapper(List<FeatureWrapper<T>> contained)
	{
		this.contained = contained;
	}

	@Override
	public Feature apply(Feature parent, T value)
	{
		List<Feature> features = this.contained.stream()
			.map(wrapper -> wrapper.apply(parent, value))
			.collect(Collectors.toList());
		return new MultiFeature(parent, features);
	}

}