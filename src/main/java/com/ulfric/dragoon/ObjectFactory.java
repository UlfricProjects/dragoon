package com.ulfric.dragoon;

import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.SkeletalFamily;
import com.ulfric.dragoon.extension.creator.CreatorExtension;
import com.ulfric.dragoon.extension.inject.InjectExtension;
import com.ulfric.dragoon.extension.intercept.InterceptExtension;
import com.ulfric.dragoon.reflect.Instances;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ObjectFactory extends SkeletalFamily<ObjectFactory> implements Factory, Extensible<Class<? extends Extension>> {

	public static ObjectFactory newInstance()
	{
		return new ObjectFactory();
	}

	private final List<Extension> extensions = new ArrayList<>();
	private final Map<Class<?>, Class<?>> bindings = new IdentityHashMap<>();

	private ObjectFactory()
	{
		this(null);
	}

	private ObjectFactory(ObjectFactory parent)
	{
		super(parent);

		this.extensions.add(new CreatorExtension(this));
		this.install(InjectExtension.class);
		this.install(InterceptExtension.class);
	}

	@Override
	public ObjectFactory createChild()
	{
		return new ObjectFactory(this);
	}

	// TODO cache type transformations
	@Override
	public void install(Class<? extends Extension> extension)
	{
		Objects.requireNonNull(extension, "extension type");

		Extension value = this.request(extension);
		Objects.requireNonNull(extension, () -> "extension instance: " + extension);
		this.extensions.add(value);
	}

	@Override
	public <T> T request(Class<T> type)
	{
		return this.request(type, new Object[0]); // TODO array constant
	}

	public <T> T request(Class<T> type, Object... parameters)
	{
		Object value = this.requestNotNull(type, parameters);
		if (type.isInstance(value))
		{
			@SuppressWarnings("unchecked")
			T casted = (T) value;
			return casted;
		}

		// TODO throw sane exception
		throw new RuntimeException();
	}

	public Object requestNotNull(Class<?> type)
	{
		return this.requestNotNull(type, new Object[0]); // TODO array constant
	}

	public Object requestNotNull(Class<?> type, Object... parameters)
	{
		Object value = this.requestUnchecked(type, parameters);
		Objects.requireNonNull(value);
		return value;
	}

	public Object requestUnchecked(Class<?> type)
	{
		return this.requestUnchecked(type, new Object[0]); // TODO array constant
	}

	public Object requestUnchecked(Class<?> type, Object... parameters)
	{
		Class<?> transformedType = this.getBinding(type);
		transformedType = this.transformType(transformedType);

		Object value = this.createValue(transformedType, parameters);
		value = this.transformValue(value);
		return value;
	}

	private Class<?> getBinding(Class<?> type)
	{
		Class<?> binding = this.bindings.get(type);

		if (binding == null || binding == type)
		{
			return type;
		}

		return this.getBinding(binding);
	}

	public Binding bind(Class<?> bind)
	{
		Objects.requireNonNull(bind, "bind");

		return new Binding(bind);
	}

	// TODO cache type transformations
	private Class<?> transformType(Class<?> type)
	{
		Class<?> transformed = type;
		for (Extension extension : this.extensions)
		{
			transformed = extension.transform(transformed);
		}
		return transformed;
	}

	private Object transformValue(Object value)
	{
		Object transformed = value;
		for (Extension extension : this.extensions)
		{
			transformed = extension.transform(transformed);
		}
		return transformed;
	}

	private Object createValue(Class<?> type, Object... parameters)
	{
		// TODO scopes n shit
		return Instances.newInstance(type, parameters);
	}

	public final class Binding
	{
		private final Class<?> bind;

		Binding(Class<?> bind)
		{
			this.bind = bind;
		}

		public void to(Class<?> implementation)
		{
			if (implementation == null)
			{
				ObjectFactory.this.bindings.remove(this.bind);
				return;
			}

			ObjectFactory.this.bindings.put(this.bind, implementation);
		}
	}

}