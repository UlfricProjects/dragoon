package com.ulfric.dragoon.container;

import java.util.List;

final class MultiFeature extends ChildFeature {

	private final List<Feature> children;

	public MultiFeature(Feature parent, List<Feature> children)
	{
		super(parent);
		this.children = children;
	}

	@Override
	public void onEnable()
	{
		this.children.forEach(Feature::enable);
	}

	@Override
	public void onDisable()
	{
		this.children.forEach(Feature::disable);
	}

}