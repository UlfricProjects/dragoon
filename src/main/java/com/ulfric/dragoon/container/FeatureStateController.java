package com.ulfric.dragoon.container;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.lang3.ClassUtils;

import com.ulfric.dragoon.ObjectFactory;

final class FeatureStateController {

	private static final Map<Class<?>, FeatureWrapper<?>> FEATURE_WRAPPERS =
			Collections.synchronizedMap(new LinkedHashMap<>());

	static <T> void registerFeatureWrapper(Class<T> request, FeatureWrapper<T> wrapper)
	{
		Objects.requireNonNull(request);
		Objects.requireNonNull(wrapper);

		FeatureStateController.FEATURE_WRAPPERS.put(request, wrapper);
	}

	private static <T> FeatureWrapper<T> getFeatureWrapper(Class<T> request)
	{
		FeatureWrapper<T> wrapper = FeatureStateController.getExactFeatureWrapper(request);
		if (wrapper != null)
		{
			return wrapper;
		}

		wrapper = FeatureStateController.getExactFeatureWrapperFromOneOf(ClassUtils.getAllSuperclasses(request));
		if (wrapper != null)
		{
			return wrapper;
		}

		wrapper = FeatureStateController.getExactFeatureWrapperFromOneOf(ClassUtils.getAllInterfaces(request));
		return wrapper;
	}

	private static <T> FeatureWrapper<T> getExactFeatureWrapperFromOneOf(List<Class<?>> requests)
	{
		for (Class<?> request : requests)
		{
			@SuppressWarnings("unchecked")
			FeatureWrapper<T> wrapper = (FeatureWrapper<T>) FeatureStateController.getExactFeatureWrapper(request);

			if (wrapper != null)
			{
				return wrapper;
			}
		}

		return null;
	}

	private static <T> FeatureWrapper<T> getExactFeatureWrapper(Class<T> request)
	{
		@SuppressWarnings("unchecked")
		FeatureWrapper<T> wrapper = (FeatureWrapper<T>) FeatureStateController.FEATURE_WRAPPERS.get(request);
		return wrapper;
	}

	public static FeatureStateController newInstance(ObjectFactory factory, Feature owner)
	{
		Objects.requireNonNull(factory);
		Objects.requireNonNull(owner);

		return new FeatureStateController(factory, owner);
	}

	private final ObjectFactory factory;
	private final Feature owner;
	private final Map<Class<?>, Feature> states = new LinkedHashMap<>();

	private FeatureStateController(ObjectFactory factory, Feature owner)
	{
		this.factory = factory;
		this.owner = owner;
	}

	void refresh()
	{
		this.states.entrySet().forEach(entry ->
		{
			if (entry.getValue() != null)
			{
				this.refreshFeature(entry.getValue());
				return;
			}

			this.installNow(entry.getKey());
			this.refreshFeature(entry.getValue());
		});
	}

	void install(Class<?> feature)
	{
		Objects.requireNonNull(feature);

		if (this.isInstalled(feature))
		{
			throw new IllegalStateException("Feature already installed: " + feature);
		}

		if (this.installLazily(feature))
		{
			return;
		}

		this.installNow(feature);
	}

	boolean isInstalled(Class<?> feature)
	{
		return this.states.containsKey(feature);
	}

	private void installNow(Class<?> feature)
	{
		Object genericImplementation = this.factory.request(feature);
		if (this.installDirectly(feature, genericImplementation))
		{
			return;
		}

		@SuppressWarnings("unchecked")
		FeatureWrapper<Object> wrapper = (FeatureWrapper<Object>) FeatureStateController.getFeatureWrapper(feature);
		if (wrapper == null)
		{
			throw new FeatureWrapperMissingException(feature);
		}

		Feature featureImplementation = wrapper.apply(this.owner, genericImplementation);
		Objects.requireNonNull(featureImplementation);
		this.forceInstall(feature, featureImplementation);
	}

	private boolean installLazily(Class<?> feature)
	{
		if (!this.owner.isEnabled())
		{
			this.states.put(feature, null);

			return true;
		}

		return false;
	}

	private boolean installDirectly(Class<?> feature, Object genericImplementation)
	{
		if (genericImplementation instanceof Feature)
		{
			Feature featureImplementation = (Feature) genericImplementation;
			this.forceInstall(feature, featureImplementation);
			return true;
		}

		return false;
	}

	private void forceInstall(Class<?> feature, Feature featureImplementation)
	{
		this.states.put(feature, featureImplementation);
		this.refreshFeature(featureImplementation);
	}

	private void refreshFeature(Feature feature)
	{
		if (this.shouldEnable(feature))
		{
			feature.enable();
		}
		else if (this.shouldDisable(feature))
		{
			feature.disable();
		}
	}

	private boolean shouldEnable(Feature feature)
	{
		return this.shouldChange(feature, Feature::isEnabled);
	}

	private boolean shouldDisable(Feature feature)
	{
		return this.shouldChange(feature, Feature::isDisabled);
	}

	private boolean shouldChange(Feature feature, Predicate<Feature> statePredicate)
	{
		return statePredicate.test(this.owner) && !statePredicate.test(feature);
	}

}