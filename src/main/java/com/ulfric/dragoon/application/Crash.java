package com.ulfric.dragoon.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Crash {

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private ApplicationState state;
		private List<Throwable> causes;

		Builder() {
		}

		public Crash build() {
			Objects.requireNonNull(state, "state");

			List<Throwable> causes = Collections.unmodifiableList(new ArrayList<>(this.causes));
			return new Crash(causes, state);
		}

		public Builder setState(ApplicationState state) {
			this.state = state;
			return this;
		}

		public Builder setCauses(List<Throwable> causes) {
			this.causes = causes;
			return this;
		}
	}

	private final List<Throwable> causes;
	private final ApplicationState state;

	Crash(List<Throwable> causes, ApplicationState state) {
		this.causes = causes;
		this.state = state;
	}

	public List<Throwable> getCauses() {
		return causes;
	}

	public ApplicationState getState() {
		return state;
	}

}
