package com.ulfric.dragoon.container;

import com.ulfric.dragoon.Binding;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.interceptors.Audit;

public class Container extends SkeletalFeature implements Extensible<Class<?>> {

	public static <T> void registerFeatureWrapper(Class<T> request, FeatureWrapper<T> wrapper)
	{
		FeatureStateController.registerFeatureWrapper(request, wrapper);
	}

	@Inject
	private ObjectFactory factory;

	private FeatureStateController features;

	@Initialize
	private void initializeFeatureStateController()
	{
		this.features = FeatureStateController.newInstance(this.factory, this);
	}

	@Override
	public final void install(Class<?> feature)
	{
		this.features.install(feature);
	}

	public final Binding bind(Class<?> request)
	{
		return this.factory.bind(request);
	}

	@Override
	public final void onStateChange()
	{
		this.features.refresh();
	}

	@Override
	@Audit("Enable")
	public void onEnable()
	{

	}

	@Override
	@Audit("Disable")
	public void onDisable()
	{

	}

	@Override
	public String toString()
	{
		return this.getName();
	}

}