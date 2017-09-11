package com.ulfric.dragoon;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.extension.inject.InjectExtension;
import com.ulfric.dragoon.extension.intercept.InterceptExtension;
import com.ulfric.dragoon.extension.intercept.asynchronous.Asynchronous;
import com.ulfric.dragoon.extension.intercept.asynchronous.AsynchronousInterceptor;
import com.ulfric.dragoon.extension.loader.LoaderExtension;
import com.ulfric.dragoon.logging.DefaultLoggerBinding;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.reflect.Instances;
import com.ulfric.dragoon.value.Result;

public final class ObjectFactory implements Factory, Extensible<Class<? extends Extension>> {

	private static final List<Class<? extends Extension>> DEFAULT_EXTENSIONS =
	        Arrays.asList(InterceptExtension.class, LoaderExtension.class);
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	private final Map<Class<? extends Extension>, Extension> extensionTypes = new IdentityHashMap<>();
	private final List<Extension> extensions = new ArrayList<>();
	private final Map<Class<?>, Binding> bindings = new IdentityHashMap<>();

	public ObjectFactory() {
		bindSelf();
		defaultExtensions();
		defaultBindings();
	}

	private void bindSelf() {
		bind(ObjectFactory.class).toValue(this);
		bind(Factory.class).toValue(this);
	}

	private void defaultExtensions() {
		install(InjectExtension.class, this);
		DEFAULT_EXTENSIONS.forEach(this::install);
	}

	private void defaultBindings() {
		bind(FileSystem.class).toValue(FileSystems.getDefault());
		bind(Logger.class).toFunction(DefaultLoggerBinding.INSTANCE);
		bind(Asynchronous.class).to(AsynchronousInterceptor.class);
	}

	@Override
	public Result install(Class<? extends Extension> extension) {
		return install(extension, ObjectFactory.EMPTY_OBJECT_ARRAY);
	}

	public Result install(Class<? extends Extension> extension, Object... parameters) {
		Objects.requireNonNull(extension, "extension type");

		ResultWrapper result = new ResultWrapper();
		result.setResult(Result.FAILURE);
		Extension value = extensionTypes.computeIfAbsent(extension, type -> {
			Extension request = request(extension, parameters);
			if (request != null) {
				result.setResult(Result.SUCCESS);
			}
			return request;
		});

		if (result.getResult() == Result.FAILURE) {
			return Result.FAILURE;
		}

		bind(extension).toValue(value);
		this.extensions.add(value);

		return Result.SUCCESS;
	}

	public Result uninstall(Class<? extends Extension> type) {
		Class<?> realType = type;
		Extension instance = extensionTypes.remove(realType);
		if (instance == null) {
			realType = Classes.getNonDynamic(realType);
			instance = extensionTypes.remove(realType);

			if (instance == null) {
				return Result.FAILURE;
			}
		}

		bind(realType).toNothing();
		extensions.remove(instance);

		return Result.SUCCESS;
	}

	@Override
	public <T> T request(Class<T> type) {
		return request(type, ObjectFactory.EMPTY_OBJECT_ARRAY);
	}

	@Override
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

	public CreateBinding bind(Class<?>... bind) {
		Objects.requireNonNull(bind, "bind");
		if (bind.length == 0) {
			throw new IllegalArgumentException("Empty bindings");
		}

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

	public final class CreateBinding {
		private final Class<?>[] bind;

		CreateBinding(Class<?>[] bind) {
			this.bind = bind;
		}

		public void toNothing() {
			for (Class<?> bind : bind) {
				bindings.remove(bind);
			}
		}

		public void to(Class<?> type) {
			Objects.requireNonNull(type, "type");

			register(new ClassBinding(type));
		}

		public void toSupplier(Supplier<?> supplier) {
			Objects.requireNonNull(supplier, "supplier");

			toFunction(ignore -> supplier.get());
		}

		public void toFunction(Function<Object[], ?> function) {
			Objects.requireNonNull(function, "function");

			register(new FunctionBinding(function));
		}

		public void toLazy(Function<Object[], ?> function) {
			Objects.requireNonNull(function, "function");

			register(new LazyBinding(function));
		}

		public void toSingleton() {
			for (Class<?> binding : bind) {
				Object value = request(binding);

				if (value == null) {
					continue;
				}

				toValue(value);
				return;
			}

			throw new IllegalArgumentException("Could not create singleton from any of " + Arrays.toString(bind));
		}

		public void toValue(Object value) {
			Objects.requireNonNull(value, "value");

			register(new ValueBinding(value));
		}

		private void register(Binding binding) {
			for (Class<?> bind : bind) {
				bindings.put(bind, binding);
			}
		}
	}

	private interface Binding {
		Object create(Object... parameters);
	}

	private class FunctionBinding implements Binding {
		private final Function<Object[], ?> function;

		FunctionBinding(Function<Object[], ?> function) {
			this.function = function;
		}

		@Override
		public Object create(Object... parameters) {
			return function.apply(parameters);
		}
	}

	private final class LazyBinding extends FunctionBinding {
		private Object value;

		LazyBinding(Function<Object[], ?> function) {
			super(function);
		}

		@Override
		public Object create(Object... parameters) {
			if (value != null) {
				return value;
			}

			synchronized (this) {
				if (value != null) {
					return value;
				}

				value = super.create(parameters);
			}

			return value;
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
			return Instances.instance(transformedType, parameters);
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
