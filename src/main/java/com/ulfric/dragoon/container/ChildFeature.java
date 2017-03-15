package com.ulfric.dragoon.container;

public class ChildFeature extends SkeletalFeature {

	public ChildFeature(Feature parent)
	{
		this.parent = parent;
	}

	protected final Feature parent;

}