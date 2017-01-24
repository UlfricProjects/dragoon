package com.ulfric.commons.cdi.container;

public class ChildComponent extends SkeletalComponent {

	public ChildComponent(Component parent)
	{
		this.parent = parent;
	}

	protected final Component parent;

}