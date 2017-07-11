package com.ulfric.dragoon;

import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.creator.CreatorExtension;
import com.ulfric.dragoon.extension.inject.InjectExtension;
import com.ulfric.dragoon.extension.intercept.InterceptExtension;
import com.ulfric.dragoon.extension.loader.LoaderExtension;
import com.ulfric.dragoon.reflect.Instances;
import com.ulfric.dragoon.value.Result;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public final class ObjectFactory implements Factory, Extensible<Class<? extends Extension>> {

	private static final List<Class<? extends Extension>> DEFAULT_EXTENSIONS =
	        Arrays.asList(InjectExtension.class, InterceptExtension.class, LoaderExtension.class);
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	private final Set<Class<? extends Extension>> extensionTypes = Collections.newSetFromMap(new IdentityHashMap<>());
	private final List<Extension> extensions = new ArrayList<>();
	private final Map<Class<?>, Binding> bindings = new IdentityHashMap<>();

	public ObjectFactory() {
		defaultExtensions();
		defaultBindings();
	}

	private void defaultExtensions() {
		install(CreatorExtension.class, this);
		DEFAULT_EXTENSIONS.forEach(this::install);
	}

	private void defaultBindings() {
		bind(FileSystem.class).to(FileSystems.getDefault());
	}

	@Override
	public Result install(Class<? extends Extension> extension) {
		return install(extension, ObjectFactory.EMPTY_OBJECT_ARRAY);
	}

	public Result install(Class<? extends Extension> extension, Object... parameters) {
		Objects.requireNonNull(extension, "extension type");

		if (!this.extensionTypes.add(extension)) {
			return Result.FAILURE;
		}

		Extension value = request(extension, parameters);

		if (value == null) {
			return Result.FAILURE;
		}

		this.extensions.add(value);

		return Result.SUCCESS;
	}

	@Override
	public <T> T request(Class<T> type) {
		return request(type, ObjectFactory.EMPTY_OBJECT_ARRAY);
	}

	public <T> T request(Class<T> type, Object... parameters) {
		Object value = requestUnspecific(type, parameters);

		try {
			return type.cast(value);
		} catch (ClassCastException exception) {
			throw new RequestFailedException(type, parameters, exception);
		}
	}

	public Object requestUnspecific(Class<?> type) {
		return requestUnspecific(type, ObjectFactory.EMPTY_OBJECT_ARRAY);
	}

	public Object requestUnspecific(Class<?> type, Object... parameters) {
		Binding binding = getBinding(type);
		if (binding == null) {
			return null;
		}

		Object value = binding.create(parameters);
		if (value == null) {
			return null;
		}
		return transformValue(value);
	}

	private Binding getBinding(Class<?> type) {
		Binding binding = bindings.get(type);
		if (binding == null) {
			bind(type).to(type);
			return getBinding(type);
		}
		return binding;
	}

	public CreateBinding bind(Class<?> bind) {
		Objects.requireNonNull(bind, "bind");

		return new CreateBinding(bind);
	}

	private Class<?> transformType(Class<?> type) {
		Class<?> transformed = type;
		for (Extension extension : extensions) {
			transformed = extension.transform(transformed);
		}
		return transformed;
	}

	private Object transformValue(Object value) {
		Object transformed = value;
		for (Extension extension : extensions) {
			transformed = extension.transform(transformed);
		}
		return transformed;
	}

	private Object createValue(Class<?> type, Object... parameters) {
		return Instances.instance(type, parameters);
	}

	public final class CreateBinding {
		private final Class<?> bind;

		CreateBinding(Class<?> bind) {
			this.bind = bind;
		}

		public void to(Object implementation) {
			if (implementation == null) {
				bindings.remove(bind);
				return;
			}

			Binding binding;

			if (implementation instanceof Class) {
				binding = new ClassBinding((Class<?>) implementation);
			} else if (implementation instanceof Function) {
				@SuppressWarnings("unchecked")
				Function<Object[], ?> casted = (Function<Object[], ?>) implementation;
				binding = new FunctionBinding(casted);
			} else if (bind.isInstance(implementation)) {
				binding = new ValueBinding(implementation);
			} else {
				throw new IllegalArgumentException("Could not bind " + bind + " to " + implementation);
			}

			bindings.put(bind, binding);
		}
	}

	private interface Binding {
		Object create(Object... parameters);
	}

	private final class FunctionBinding implements Binding {
		private final Function<Object[], ?> function;

		FunctionBinding(Function<Object[], ?> function) {
			this.function = function;
		}

		@Override
		public Object create(Object... parameters) {
			return function.apply(parameters);
		}
	}

	private final class ClassBinding implements Binding {
		private final Class<?> type;

		ClassBinding(Class<?> type) {
			this.type = type;
		}

		@Override
		public Object create(Object... parameters) {
			Class<?> transformedType = transformType(type);
			return createValue(transformedType, parameters);
		}
	}

	private final class ValueBinding implements Binding {
		private final Object value;

		ValueBinding(Object value) {
			this.value = value;
		}

		@Override
		public Object create(Object... parameters) {
			return value;
		}
	}

}
