package com.ulfric.dragoon.application;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.loader.Loader;
import com.ulfric.dragoon.extension.loader.OwnedClassLoader;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.value.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Loader
public class Container extends Application implements Extensible<Class<?>> {

	private static final Pattern CAMEL_TO_DASH = Pattern.compile("([a-z])([A-Z]+)");
	private static final AtomicInteger ID_COUNTER = new AtomicInteger();

	public static Container getOwningContainer(Object object) {
		ClassLoader loader = object.getClass().getClassLoader();

		if (loader instanceof OwnedClassLoader) {
			OwnedClassLoader owned = (OwnedClassLoader) loader;

			Object owner = owned.getOwner();
			if (owner instanceof Container) {
				return (Container) owner;
			}
		}

		return object instanceof Container ? (Container) object : null;
	}

	@Inject
	private ObjectFactory factory;

	@Inject(optional = true)
	private Logger logger;

	private final Set<Class<?>> applicationTypes = Collections.newSetFromMap(new IdentityHashMap<>());
	private final List<Application> applications = new ArrayList<>();
	private String name;

	public Container() {
		addBootHook(() -> log("Booting " + getName()));
		addBootHook(this::bootApplications);

		addShutdownHook(this::shutdownApplications);
		addShutdownHook(() -> log("Shutting down " + getName()));
	}

	public String getName() {
		if (name != null) {
			return name;
		}

		name = resolveName();
		return name;
	}

	private String resolveName() {
		String name = Classes.getNonDynamic(getClass()).getSimpleName();
		if (name.equals(Container.class.getSimpleName())) {
			return name + '#' + ID_COUNTER.getAndIncrement();
		}
		if (name.endsWith("Container")) {
			name = name.substring(0, "Container".length() - 1);
			return CAMEL_TO_DASH.matcher(name)
					.replaceAll("$1-$2")
					.toLowerCase();
		}
		return name;
	}

	private void bootApplications() {
		applications.forEach(this::update);
	}

	private void shutdownApplications() {
		ListIterator<Application> reverse = applications.listIterator(applications.size());
		while (reverse.hasPrevious()) {
			update(reverse.previous());
		}
	}

	@Override
	public Result install(Class<?> application) {
		Result validation = validate(application);
		if (!validation.isSuccess()) {
			return validation;
		}

		if (!isRunning()) {
			addBootHook(() -> install(application));
			return Result.DELAYED;
		}

		Class<?> implementation = getAsOwnedClass(application);
		Object rawInstall = getFactory().request(implementation);

		if (rawInstall == null) {
			return Result.FAILURE;
		}

		Application install = Feature.wrap(rawInstall);
		if (install == null) {
			return Result.FAILURE;
		}

		applicationTypes.add(application);
		applications.add(install);
		update(install);

		return Result.SUCCESS;
	}

	private <T> Class<? extends T> getAsOwnedClass(Class<T> type) {
		if (type.isAnnotationPresent(Loader.class)) {
			return type;
		}
		return Classes.translate(type, getClass().getClassLoader());
	}

	private Result validate(Class<?> application) {
		Objects.requireNonNull(application, "application");

		if (application == getClass()) {
			return Result.FAILURE;
		}

		if (applicationTypes.contains(application)) {
			return Result.FAILURE;
		}

		return Result.SUCCESS;
	}

	private ObjectFactory getFactory() {
		if (factory == null) {
			factory = new ObjectFactory();
		}

		return factory;
	}

	protected final void log(String message) {
		if (logger == null) {
			return;
		}

		logger.info(message);
	}

	private void update(Application application) {
		if (isRunning()) {
			application.boot();
			return;
		}

		application.shutdown();
	}

}
