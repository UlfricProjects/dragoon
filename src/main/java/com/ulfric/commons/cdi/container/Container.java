package com.ulfric.commons.cdi.container;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import org.apache.commons.lang3.ClassUtils;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.collect.MapUtils;

public class Container implements Component {

	private static final Map<Class<?>, ComponentWrapper<?>> COMPONENT_WRAPPERS =
			MapUtils.newSynchronizedIdentityHashMap();

	public static <T> void registerComponentWrapper(Class<T> request, ComponentWrapper<T> wrapper)
	{
		Objects.requireNonNull(request);
		Objects.requireNonNull(wrapper);

		Container.COMPONENT_WRAPPERS.put(request, wrapper);
	}

	private static <T> ComponentWrapper<T> getComponentWrapper(Class<T> request)
	{
		ComponentWrapper<T> wrapper = Container.getExactComponentWrapper(request);
		if (wrapper != null)
		{
			return wrapper;
		}

		wrapper = Container.getExactComponentWrapperFromOneOf(ClassUtils.getAllSuperclasses(request));
		if (wrapper != null)
		{
			return wrapper;
		}

		wrapper = Container.getExactComponentWrapperFromOneOf(ClassUtils.getAllInterfaces(request));
		return wrapper;
	}

	private static <T> ComponentWrapper<T> getExactComponentWrapperFromOneOf(List<Class<?>> requests)
	{
		for (Class<?> request : requests)
		{
			@SuppressWarnings("unchecked")
			ComponentWrapper<T> wrapper = (ComponentWrapper<T>) Container.getExactComponentWrapper(request);

			if (wrapper != null)
			{
				return wrapper;
			}
		}

		return null;
	}

	private static <T> ComponentWrapper<T> getExactComponentWrapper(Class<T> request)
	{
		@SuppressWarnings("unchecked")
		ComponentWrapper<T> wrapper = (ComponentWrapper<T>) Container.COMPONENT_WRAPPERS.get(request);
		return wrapper;
	}

	private final ComponentStateController components = new ComponentStateController(this);
	private boolean loaded;
	private boolean enabled;

	@Inject
	private ObjectFactory factory;

	public void install(Class<?> component)
	{
		Objects.requireNonNull(component);

		Object genericImplementation = this.factory.request(component);
		if (this.installDirectly(genericImplementation))
		{
			return;
		}

		@SuppressWarnings("unchecked")
		ComponentWrapper<Object> wrapper = (ComponentWrapper<Object>) Container.getComponentWrapper(component);
		if (wrapper == null)
		{
			throw new ComponentWrapperMissingException(component);
		}

		Component instance = wrapper.apply(this, genericImplementation);
		Objects.requireNonNull(instance);
		this.components.install(instance);
	}

	private boolean installDirectly(Object genericImplementation)
	{
		if (genericImplementation instanceof Component)
		{
			Component instance = (Component) genericImplementation;
			this.components.install(instance);
			return true;
		}

		return false;
	}

	@Override
	public final boolean isLoaded()
	{
		return this.loaded;
	}

	@Override
	public final boolean isEnabled()
	{
		return this.enabled;
	}

	@Override
	public final void load()
	{
		this.verify(this::isUnloaded);

		this.onLoad();
		this.loaded = true;
		this.notifyComponents();
	}

	@LogLoad
	public void onLoad()
	{

	}

	@Override
	public final void enable()
	{
		this.verify(this::isDisabled);

		this.loadIfNotLoaded();

		this.onEnable();
		this.enabled = true;
		this.notifyComponents();
	}

	private void loadIfNotLoaded()
	{
		if (!this.isLoaded())
		{
			this.load();
		}
	}

	@LogEnable
	public void onEnable()
	{

	}

	@Override
	public final void disable()
	{
		this.verify(this::isEnabled);

		this.onDisable();
		this.enabled = false;
		this.notifyComponents();
	}

	@LogDisable
	public void onDisable()
	{

	}

	private void verify(BooleanSupplier flag)
	{
		if (flag.getAsBoolean())
		{
			return;
		}

		throw new IllegalStateException("Failed to verify state: " + this.getVerifyCallerMethodName());
	}

	private String getVerifyCallerMethodName()
	{
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}

	private void notifyComponents()
	{
		this.components.refresh();
	}

}