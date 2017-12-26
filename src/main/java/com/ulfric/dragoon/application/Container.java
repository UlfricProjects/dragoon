package com.ulfric.dragoon.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.logging.Log;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.value.Result;

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

	public static ManagedContainer launch() {
		return launch(ManagedContainer.class);
	}

	public static <T extends Container> T launch(Class<T> container) {
		ObjectFactory factory = new ObjectFactory();
		T managed = factory.request(container);
		addShutdownHook(managed);
		managed.boot();
		return managed;
	}

	private static void addShutdownHook(Container container) {
		Runtime.getRuntime().addShutdownHook(
				new Thread(container::shutdown, "container-" + container.getName() + "-shutdown-hook"));
	}

	@Inject
	private ObjectFactory factory;

	@Inject
	private Log logger;

	private final Set<Class<?>> applicationTypes = Collections.newSetFromMap(new IdentityHashMap<>());
	private final List<Application> applications = new ArrayList<>();
	private String name;

	final ThreadClassLoaderState state = new ThreadClassLoaderState(getClass().getClassLoader());

	public Container() {
		addBootHook(() -> log("Booting " + getName()));
		addBootHook(this::bootApplications);

		addShutdownHook(() -> log("Shutting down " + getName()));
		addShutdownHook(this::shutdownApplications);
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
			name = name.substring(0, name.length() - "Container".length());
			return CAMEL_TO_DASH.matcher(name).replaceAll("$1-$2").toLowerCase();
		}
		return name;
	}

	private void bootApplications() {
		state.doContextual(() -> applications.forEach(this::update));
	}

	private void shutdownApplications() {
		state.doContextual(() -> {
			ListIterator<Application> reverse = applications.listIterator(applications.size());
			while (reverse.hasPrevious()) {
				update(reverse.previous());
			}
		});
	}

	@Override
	public final Result install(Class<?> application) {
		Result validation = validate(application);
		if (!validation.isSuccess()) {
			return validation;
		}

		if (!canBootApplications()) {
			addBootHook(() -> auditedInstall(application));
			return Result.DELAYED;
		}

		Class<?> implementation = getAsOwnedClass(application);
		Object rawInstall = getFactory().request(implementation);

		if (rawInstall == null) {
			System.out.println(application + " FAILURE 1");
			return Result.FAILURE;
		}

		Application install = rawInstall instanceof Application ? (Application) rawInstall : Feature.wrap(rawInstall);
		if (install == null) {
			System.out.println(application + " FAILURE 2");
			return Result.FAILURE;
		}

		applicationTypes.add(application);
		applications.add(install);
		update(install);

		return Result.SUCCESS;
	}

	private void auditedInstall(Class<?> application) {
		Result install = install(application);
		if (!install.isSuccess()) {
			logger.severe("Failed to install " + Classes.getNonDynamic(application));
		}
	}

	protected void log(String message) {
		logger.info(message);
	}

	private <T> Class<? extends T> getAsOwnedClass(Class<T> type) {
		ClassLoader thisLoader = getClass().getClassLoader();
		if (type.getClassLoader().equals(thisLoader)) {
			return type;
		}
		return Classes.translate(type, thisLoader);
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

	private void update(Application application) {
		if (canBootApplications()) {
			application.boot();
			return;
		}

		application.shutdown();
	}

	private boolean canBootApplications() {
		ApplicationState state = getState();
		return state == ApplicationState.BOOT || state == ApplicationState.RUNTIME;
	}

}
