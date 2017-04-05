package com.ulfric.dragoon.container;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.interceptors.Audit;

public class Container extends SkeletalFeature {

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

	public final void install(Class<?> feature)
	{
		this.features.install(feature);
	}

	@Override
	public final void onStateChange()
	{
		this.features.refresh();
	}

	@Override
	@Audit("Load")
	public void onLoad()
	{

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