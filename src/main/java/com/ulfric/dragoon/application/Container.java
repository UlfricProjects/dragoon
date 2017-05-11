package com.ulfric.dragoon.application;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.extension.creator.Creator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;

public class Container extends Application implements Extensible<Class<? extends Application>> {

	@Creator
	private ObjectFactory factory;

	private final Set<Class<?>> installedApplications = Collections.newSetFromMap(new IdentityHashMap<>());
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
	public InstallApplicationResult install(Class<? extends Application> application)
	{
		InstallApplicationResult validation = this.validate(application);
		if (!validation.isSuccess())
		{
			return validation;
		}

		Application install = this.getFactory().request(application);
		this.applications.add(install);
		this.update(install);

		return InstallApplicationResult.SUCCESS;
	}

	private InstallApplicationResult validate(Class<?> application)
	{
		Objects.requireNonNull(application, "application");

		if (application == this.getClass())
		{
			return InstallApplicationResult.SELF_INSTALLATION;
		}

		if (!this.installedApplications.add(application))
		{
			return InstallApplicationResult.ALREADY_INSTALLED;
		}

		return InstallApplicationResult.SUCCESS;
	}

	private ObjectFactory getFactory()
	{
		if (this.factory != null)
		{
			return this.factory;
		}

		return this.factory = ObjectFactory.newInstance();
	}

	private void update(Application application)
	{
		if (this.isRunning())
		{
			if (application.isRunning())
			{
				return;
			}

			application.start();
			return;
		}

		if (application.isRunning())
		{
			application.shutdown();
		}
	}

}