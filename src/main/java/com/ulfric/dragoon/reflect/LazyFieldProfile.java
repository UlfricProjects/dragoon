package com.ulfric.dragoon.reflect;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.ulfric.dragoon.value.Lazy;

public final class LazyFieldProfile implements Consumer<Object>, Predicate<Class<?>> {

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

	@Override
	public boolean test(Class<?> type) {
		if (loading) {
			return false;
		}

		return wrap.get().containsFields(type);
	}

}
