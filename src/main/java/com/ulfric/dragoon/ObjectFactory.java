package com.ulfric.dragoon;

import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.Result;
import com.ulfric.dragoon.extension.creator.CreatorExtension;
import com.ulfric.dragoon.extension.inject.InjectExtension;
import com.ulfric.dragoon.extension.intercept.InterceptExtension;
import com.ulfric.dragoon.extension.loader.LoaderExtension;
import com.ulfric.dragoon.reflect.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ObjectFactory implements Factory, Extensible<Class<? extends Extension>> {

	private static final List<Class<? extends Extension>> DEFAULT_EXTENSIONS =
	        Arrays.asList(InjectExtension.class, InterceptExtension.class, LoaderExtension.class);
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	private final Set<Class<? extends Extension>> extensionTypes = Collections.newSetFromMap(new IdentityHashMap<>());
	private final List<Extension> extensions = new ArrayList<>();
	private final Map<Class<?>, Class<?>> bindings = new IdentityHashMap<>();

	public ObjectFactory() {
		this(ObjectFactory.DEFAULT_EXTENSIONS);
	}

	private ObjectFactory(List<Class<? extends Extension>> extensions) {
		this.install(CreatorExtension.class, this).isSuccess();
		extensions.forEach(this::install);
	}

	@Override
	public Result install(Class<? extends Extension> extension) {
		return this.install(extension, ObjectFactory.EMPTY_OBJECT_ARRAY);
	}

	public Result install(Class<? extends Extension> extension, Object... parameters) {
		Objects.requireNonNull(extension, "extension type");

		if (!this.extensionTypes.add(extension)) {
			return Result.FAILURE;
		}

		Extension value = this.request(extension, parameters);

		if (value == null) {
			return Result.FAILURE;
		}

		this.extensions.add(value);

		return Result.SUCCESS;
	}

	@Override
	public <T> T request(Class<T> type) {
		return this.request(type, ObjectFactory.EMPTY_OBJECT_ARRAY);
	}

	public <T> T request(Class<T> type, Object... parameters) {
		Object value = this.requestUnspecific(type, parameters);

		try {
			return type.cast(value);
		} catch (ClassCastException exception) {
			throw new RequestFailedException(type, parameters, exception);
		}
	}

	public Object requestUnspecific(Class<?> type) {
		return this.requestUnspecific(type, ObjectFactory.EMPTY_OBJECT_ARRAY);
	}

	public Object requestUnspecific(Class<?> type, Object... parameters) {
		Class<?> transformedType = this.getBinding(type);
		transformedType = this.transformType(transformedType);

		Object value = this.createValue(transformedType, parameters);
		if (value != null) {
			value = this.transformValue(value);
		}
		return value;
	}

	private Class<?> getBinding(Class<?> type) {
		Class<?> binding = this.bindings.get(type);

		if (binding == null || binding == type) {
			return type;
		}

		return this.getBinding(binding);
	}

	public Binding bind(Class<?> bind) {
		Objects.requireNonNull(bind, "bind");

		return new Binding(bind);
	}

	private Class<?> transformType(Class<?> type) {
		Class<?> transformed = type;
		for (Extension extension : this.extensions) {
			transformed = extension.transform(transformed);
		}
		return transformed;
	}

	private Object transformValue(Object value) {
		Object transformed = value;
		for (Extension extension : this.extensions) {
			transformed = extension.transform(transformed);
		}
		return transformed;
	}

	private Object createValue(Class<?> type, Object... parameters) {
		return Instances.newInstance(type, parameters);
	}

	public final class Binding {
		private final Class<?> bind;

		Binding(Class<?> bind) {
			this.bind = bind;
		}

		public void to(Class<?> implementation) {
			if (implementation == null) {
				ObjectFactory.this.bindings.remove(this.bind);
				return;
			}

			ObjectFactory.this.bindings.put(this.bind, implementation);
		}
	}

}
