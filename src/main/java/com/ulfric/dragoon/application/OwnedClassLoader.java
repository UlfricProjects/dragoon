package com.ulfric.dragoon.application;

import java.util.Objects;

public class OwnedClassLoader extends ClassLoader {

	private Object owner;

	public OwnedClassLoader(ClassLoader parent) {
		super(parent);
	}

	public Object getOwner() {
		return this.owner;
	}

	public void setOwner(Object owner) {
		Objects.requireNonNull(owner, "owner");

		if (this.owner != null) {
			if (owner == this.owner) {
				return;
			}

			throw new IllegalStateException(
			        this + " is already owned by " + this.owner + ", could not overload with " + owner);
		}

		this.owner = owner;
	}

}
