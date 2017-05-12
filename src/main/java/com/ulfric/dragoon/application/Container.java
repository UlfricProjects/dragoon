package com.ulfric.dragoon.application;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.extension.Result;
import com.ulfric.dragoon.extension.creator.Creator;
import com.ulfric.dragoon.extension.loader.Loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;

@Loader
public class Container extends Application implements Extensible<Class<? extends Application>> {

	@Creator
	private ObjectFactory factory;

	private final Set<Class<?>> applicationTypes = Collections.newSetFromMap(new IdentityHashMap<>());
	private final List<Application> applications = new ArrayList<>();

	public Container()
	{
		this.addStartHook(this::startApplications);
		this.addStartHook(this::setup);

		this.addShutdownHook(this::stopApplications);
	}

	private void startApplications()
	{
		this.applications.forEach(this::update);
	}

	protected void stopApplications()
	{
		ListIterator<Application> reverse = this.applications.listIterator(this.applications.size());
		while (reverse.hasPrevious())
		{
			this.update(reverse.previous());
		}
	}

	public void setup() { }

	@Override
	public Result install(Class<? extends Application> application)
	{
		Result validation = this.validate(application);
		if (!validation.isSuccess())
		{
			return validation;
		}

		Application install = this.getFactory().request(application);

		if (install == null)
		{
			return Result.FAILURE;
		}

		this.applicationTypes.add(application);
		this.applications.add(install);
		this.update(install);

		return Result.SUCCESS;
	}

	private Result validate(Class<?> application)
	{
		Objects.requireNonNull(application, "application");

		if (application == this.getClass())
		{
			return Result.FAILURE;
		}

		if (this.applicationTypes.contains(application))
		{
			return Result.FAILURE;
		}

		return Result.SUCCESS;
	}

	private ObjectFactory getFactory()
	{
		if (this.factory != null)
		{
			return this.factory;
		}

		return this.factory = new ObjectFactory();
	}

	private void update(Application application)
	{
		if (this.isRunning())
		{
			application.start();
			return;
		}

		application.shutdown();
	}

}