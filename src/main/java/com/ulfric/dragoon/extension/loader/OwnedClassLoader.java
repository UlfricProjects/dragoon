package com.ulfric.dragoon.extension.loader;

import java.util.Objects;

public class OwnedClassLoader extends ClassLoader {

	private final ClassLoader delegate;
	private Object owner;

	public OwnedClassLoader(ClassLoader delegate)
	{
		this.delegate = delegate;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		Class<?> loaded = this.delegate.loadClass(name);
		return loaded;
	}

	public Object getOwner()
	{
		return this.owner;
	}

	public void setOwner(Object owner)
	{
		Objects.requireNonNull(owner, "owner");

		if (this.owner != null)
		{
			if (owner == this.owner)
			{
				return;
			}

			throw new IllegalStateException(this + " is already owned by " + this.owner
					+ ", could not overload with " + owner);
		}

		this.owner = owner;
	}

}