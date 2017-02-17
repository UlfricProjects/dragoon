package com.ulfric.commons.cdi.container;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;

public class Container extends SkeletalFeature {

	private static final Map<Class<?>, FeatureWrapper<?>> FEATURE_WRAPPERS =
			Collections.synchronizedMap(new LinkedHashMap<>());

	public static <T> void registerFeatureWrapper(Class<T> request, FeatureWrapper<T> wrapper)
	{
		Objects.requireNonNull(request);
		Objects.requireNonNull(wrapper);

		Container.FEATURE_WRAPPERS.put(request, wrapper);
	}

	private static <T> FeatureWrapper<T> getFeatureWrapper(Class<T> request)
	{
		FeatureWrapper<T> wrapper = Container.getExactFeatureWrapper(request);
		if (wrapper != null)
		{
			return wrapper;
		}

		wrapper = Container.getExactFeatureWrapperFromOneOf(ClassUtils.getAllSuperclasses(request));
		if (wrapper != null)
		{
			return wrapper;
		}

		wrapper = Container.getExactFeatureWrapperFromOneOf(ClassUtils.getAllInterfaces(request));
		return wrapper;
	}

	private static <T> FeatureWrapper<T> getExactFeatureWrapperFromOneOf(List<Class<?>> requests)
	{
		for (Class<?> request : requests)
		{
			@SuppressWarnings("unchecked")
			FeatureWrapper<T> wrapper = (FeatureWrapper<T>) Container.getExactFeatureWrapper(request);

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
		FeatureWrapper<T> wrapper = (FeatureWrapper<T>) Container.FEATURE_WRAPPERS.get(request);
		return wrapper;
	}

	private final FeatureStateController features = new FeatureStateController(this);

	@Inject
	private ObjectFactory factory;

	public void install(Class<?> feature)
	{
		Objects.requireNonNull(feature);

		Object genericImplementation = this.factory.request(feature);
		if (this.installDirectly(genericImplementation))
		{
			return;
		}

		@SuppressWarnings("unchecked")
		FeatureWrapper<Object> wrapper = (FeatureWrapper<Object>) Container.getFeatureWrapper(feature);
		if (wrapper == null)
		{
			throw new FeatureWrapperMissingException(feature);
		}

		Feature instance = wrapper.apply(this, genericImplementation);
		Objects.requireNonNull(instance);
		this.features.install(instance);
	}

	private boolean installDirectly(Object genericImplementation)
	{
		if (genericImplementation instanceof Feature)
		{
			Feature instance = (Feature) genericImplementation;
			this.features.install(instance);
			return true;
		}

		return false;
	}

	@Override
	public final void onStateChange()
	{
		this.features.refresh();
	}

	@Override
	@LogLoad
	public void onLoad()
	{

	}

	@Override
	@LogEnable
	public void onEnable()
	{

	}

	@Override
	@LogDisable
	public void onDisable()
	{

	}

}