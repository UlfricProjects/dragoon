package com.ulfric.dragoon.container;

import java.lang.reflect.Field;
import java.util.Map;

import com.ulfric.commons.exception.Try;

public class TestContainer extends Container {

	public static void clearFeatureWrappers()
	{
		Try.to(() ->
		{
			Field field = FeatureStateController.class.getDeclaredField("FEATURE_WRAPPERS");
			field.setAccessible(true);
			Map<?, ?> map = (Map<?, ?>) field.get(null);
			map.clear();
		});
	}

}