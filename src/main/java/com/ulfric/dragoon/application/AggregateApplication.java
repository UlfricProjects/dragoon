package com.ulfric.dragoon.application;

import java.util.List;
import java.util.Objects;

public final class AggregateApplication extends Application {

	private final List<Application> applications;

	public AggregateApplication(List<Application> applications) {
		Objects.requireNonNull(applications, "applications");

		this.applications = applications;

		addBootHook(this::bootApplications);
		addShutdownHook(this::shutdownApplications);
	}

	private void bootApplications() {
		applications.forEach(Application::boot);
	}

	private void shutdownApplications() {
		applications.forEach(Application::shutdown);
	}

}
