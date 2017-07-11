package com.ulfric.dragoon.application;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.extension.creator.Creator;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.loader.Loader;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.dragoon.value.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Loader
public class Container extends Application implements Extensible<Class<? extends Application>> {

	@Creator
	private ObjectFactory factory;

	@Inject(optional = true)
	private Logger logger;

	private final Set<Class<?>> applicationTypes = Collections.newSetFromMap(new IdentityHashMap<>());
	private final List<Application> applications = new ArrayList<>();
	private String name;
	private boolean hasSetup;

	public Container() {
		addStartHook(() -> log("Booting " + getName())); // TODO AOP auditing
		addStartHook(this::startApplications);
		addStartHook(this::runSetup);

		addShutdownHook(this::stopApplications);
		addShutdownHook(() -> log("Shutting down " + getName())); // TODO AOP auditing
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
			return name + '-' + UUID.randomUUID();
		}
		return name;
	}

	private void startApplications() {
		applications.forEach(this::update);
	}

	protected void stopApplications() {
		ListIterator<Application> reverse = applications.listIterator(applications.size());
		while (reverse.hasPrevious()) {
			update(reverse.previous());
		}
	}

	private void runSetup() {
		if (hasSetup) {
			return;
		}

		hasSetup = true;
		setup();
	}

	public void setup() {}

	@Override
	public Result install(Class<? extends Application> application) {
		Result validation = validate(application);
		if (!validation.isSuccess()) {
			return validation;
		}

		Class<? extends Application> implementation = getAsOwnedClass(application);
		Application install = getFactory().request(implementation);

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
		if (factory != null) {
			return factory;
		}

		return factory = new ObjectFactory();
	}

	private void log(String message) { // TODO AOP auditing
		if (logger == null) {
			return;
		}

		logger.info(message);
	}

	private void update(Application application) {
		if (isRunning()) {
			application.start();
			return;
		}

		application.shutdown();
	}

}
