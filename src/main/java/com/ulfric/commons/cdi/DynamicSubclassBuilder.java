package com.ulfric.commons.cdi;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;

final class DynamicSubclassBuilder<T> {

	private final Class<T> parent;
	private DynamicType.Builder<T> builder;

	public DynamicSubclassBuilder(Class<T> parent)
	{
		this.parent = parent;
		this.builder = this.createNewBuilder();
	}

	private DynamicType.Builder<T> createNewBuilder()
	{
		return new ByteBuddy().subclass(this.parent);
	}

	public Class<? extends T> build()
	{
		return this.builder.make().load(this.getParentLoader()).getLoaded();
	}

	private ClassLoader getParentLoader()
	{
		return this.parent.getClassLoader();
	}

}