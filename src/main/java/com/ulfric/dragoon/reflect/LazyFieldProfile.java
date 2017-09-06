package com.ulfric.dragoon.reflect;

import com.ulfric.dragoon.value.Lazy;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LazyFieldProfile implements Consumer<Object> {

	private final Lazy<FieldProfile> wrap;
	private boolean loading;

	public LazyFieldProfile(Supplier<FieldProfile> wrap) {
		Objects.requireNonNull(wrap, "wrap");

		Supplier<FieldProfile> loadingProfile = () -> {
			loading = true;

			FieldProfile value = wrap.get();

			loading = false;

			return value;
		};
		this.wrap = Lazy.of(loadingProfile);
	}

	@Override
	public void accept(Object setValues) {
		if (loading) {
			return;
		}

		wrap.get().accept(setValues);
	}

}
