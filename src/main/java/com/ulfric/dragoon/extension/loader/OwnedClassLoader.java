package com.ulfric.dragoon.extension.loader;

import com.ulfric.dragoon.reflect.Classes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class OwnedClassLoader extends ClassLoader {

	private Object owner;

	public OwnedClassLoader(ClassLoader parent)
	{
		super(parent);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		return this.findClass(name);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		Class<?> loaded = this.findLoadedClass(name);
		if (loaded == null)
		{
			if (this.isJdkClass(name))
			{
				return super.loadClass(name);
			}
			return this.forceLoad(name);
		}

		if (Classes.isDescendedFromClassLoader(loaded, OwnedClassLoader.class))
		{
			return loaded;
		}

		if (this.shouldOverride(loaded))
		{
			return this.reloadClass(loaded);
		}

		return loaded;
	}

	public Class<?> reloadClass(Class<?> clazz) throws ClassNotFoundException
	{
		return this.forceLoad(clazz.getName());
	}

	private Class<?> forceLoad(String name) throws ClassNotFoundException
	{
		byte[] bytes = this.getResourceBytes(name);
		if (bytes != null)
		{
			return this.defineClass(name, bytes, 0, bytes.length);
		}
		return super.loadClass(name);
	}

	private boolean shouldOverride(Class<?> type)
	{
		return !(type.isPrimitive()
				|| type.isInterface()
				|| type.isArray()
				|| type.isEnum()
				|| type.isAnnotation()
				|| this.isJdkClass(type.getName())
				|| Classes.isDescendedFromClassLoader(type, OwnedClassLoader.class));
	}

	private boolean isJdkClass(String name)
	{
		return name.startsWith("java.");
	}

	private byte[] getResourceBytes(String name)
	{
		String resource = name.replace('.', '/') + ".class";
		InputStream stream = this.getResourceAsStream(resource);

		if (stream == null)
		{
			return null;
		}

		return this.getInputStreamBytes(stream);
	}

	private byte[] getInputStreamBytes(InputStream stream)
	{
		try
		{
			ByteArrayOutputStream array = new ByteArrayOutputStream();
			int read = stream.read();
			while (read != -1)
			{
				array.write(read);
				read = stream.read();
			}
			return array.toByteArray();
		}
		catch (IOException thatsOk)
		{
			return null;
		}
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