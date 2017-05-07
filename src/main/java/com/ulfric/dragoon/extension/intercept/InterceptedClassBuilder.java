package com.ulfric.dragoon.extension.intercept;

import com.ulfric.dragoon.Dynamic;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.creator.Creator;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;

public class InterceptedClassBuilder<T> {

	@Creator
	private ObjectFactory factory;

	private final Class<T> parent;
	private final DynamicType.Builder<T> builder;

	public InterceptedClassBuilder(Class<T> parent)
	{
		this.parent = parent;
		this.builder = new ByteBuddy().subclass(parent).implement(Dynamic.class);
	}

	public Class<? extends T> build()
	{
		this.run();
		return this.builder.make().load(this.parent.getClassLoader()).getLoaded();
	}

	private void run()
	{
		
	}

}