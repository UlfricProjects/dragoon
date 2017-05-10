package com.ulfric.dragoon.extension;

public abstract class SkeletalFamily<T extends SkeletalFamily<T>> implements Family<T> {

	private final T parent;

	public SkeletalFamily()
	{
		this(null);
	}

	public SkeletalFamily(T parent)
	{
		this.parent = parent;
	}

	public final boolean hasParent()
	{
		return this.parent != null;
	}

	@Override
	public final T getParent()
	{
		return this.parent;
	}

}