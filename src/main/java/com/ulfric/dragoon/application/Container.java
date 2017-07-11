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
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Loader
public class Container extends Application implements Extensible<Class<? extends Application>> {

	@Creator
	private ObjectFactory factory;

	@Inject(optional = true)
	private Logger logger; // TODO AOP auditing

	private final Set<Class<?>> applicationTypes = Collections.newSetFromMap(new IdentityHashMap<>());
	private final List<Application> applications = new ArrayList<>();
	private String name;
	private boolean hasSetup;

	public Container() {
		this.addStartHook(() -> this.log("Booting " + this.getName())); // TODO AOP auditing
		this.addStartHook(this::startApplications);
		this.addStartHook(this::runSetup);

		this.addShutdownHook(this::stopApplications);
		this.addShutdownHook(() -> this.log("Shutting down " + this.getName())); // TODO AOP auditing
	}

	public String getName() {
		if (this.name != null) {
			return this.name;
		}

		this.name = this.resolveName();
		return this.name;
	}

	private String resolveName() {
		String name = Classes.getNonDynamic(this.getClass()).getSimpleName();
		if (name.equals(Container.class.getSimpleName())) {
			return name + '-' + UUID.randomUUID();
		}
		return name;
	}

	private void startApplications() {
		this.applications.forEach(this::update);
	}

	protected void stopApplications() {
		ListIterator<Application> reverse = this.applications.listIterator(this.applications.size());
		while (reverse.hasPrevious()) {
			this.update(reverse.previous());
		}
	}

	private void runSetup() {
		if (this.hasSetup) {
			return;
		}

		this.hasSetup = true;
		this.setup();
	}

	public void setup() {}

	@Override
	public Result install(Class<? extends Application> application) {
		Result validation = this.validate(application);
		if (!validation.isSuccess()) {
			return validation;
		}

		Class<? extends Application> implementation = this.getAsOwnedClass(application);
		Application install = this.getFactory().request(implementation);

		if (install == null) {
			return Result.FAILURE;
		}

		this.applicationTypes.add(application);
		this.applications.add(install);
		this.update(install);

		return Result.SUCCESS;
	}

	private <T> Class<? extends T> getAsOwnedClass(Class<T> type) {
		if (type.isAnnotationPresent(Loader.class)) {
			return type;
		}
		return Classes.translate(type, this.getClass().getClassLoader());
	}

	private Result validate(Class<?> application) {
		Objects.requireNonNull(application, "application");

		if (application == this.getClass()) {
			return Result.FAILURE;
		}

		if (this.applicationTypes.contains(application)) {
			return Result.FAILURE;
		}

		return Result.SUCCESS;
	}

	private ObjectFactory getFactory() {
		if (this.factory != null) {
			return this.factory;
		}

		return this.factory = new ObjectFactory();
	}

	private void log(String message) // TODO AOP auditing
	{
		Logger logger = this.getLogger();
		if (logger == null) {
			System.out.println('[' + this.getName() + "] " + message);
			return;
		}
		logger.info(message);
	}

	private Logger getLogger() // TODO AOP auditing
	{
		if (this.logger != null) {
			return this.logger;
		}

		return this.logger = LogManager.getLogManager().getLogger(this.getName());
	}

	private void update(Application application) {
		if (this.isRunning()) {
			application.start();
			return;
		}

		application.shutdown();
	}

}
