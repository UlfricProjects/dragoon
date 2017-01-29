package com.ulfric.commons.cdi.container;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;

public class Container extends SkeletalComponent {

	private static final Map<Class<?>, ComponentWrapper<?>> COMPONENT_WRAPPERS =
			Collections.synchronizedMap(new LinkedHashMap<>());

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
	public final void onStateChange()
	{
		this.components.refresh();
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