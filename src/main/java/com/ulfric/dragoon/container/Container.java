package com.ulfric.dragoon.container;

import com.ulfric.dragoon.Binding;
import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.interceptors.Audit;
import com.ulfric.dragoon.scope.SingletonScope;
import com.ulfric.dragoon.scope.SingletonScopeStrategy;

@SingletonScope
public class Container extends SkeletalFeature implements Factory, Extensible<Class<?>> {

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

	@Initialize
	private void bindSelfToChildren()
	{
		this.factory.bind(this.getClass()).to(this.getClass());
		this.factory.bind(SingletonScope.class).to(SingletonScopeStrategy.class);
		SingletonScopeStrategy scope = (SingletonScopeStrategy) this.factory.request(SingletonScope.class);
		scope.setInstance(this);
	}

	@Override
	public final void install(Class<?> feature)
	{
		this.features.install(feature);
	}

	@Override
	public final Binding bind(Class<?> request)
	{
		return this.factory.bind(request);
	}

	@Override
	public final Object request(Class<?> request)
	{
		return this.factory.request(request);
	}

	@Override
	public final <T> T requestExact(Class<T> request)
	{
		return this.factory.requestExact(request);
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