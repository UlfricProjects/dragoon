package com.ulfric.dragoon.interceptors;

import java.util.concurrent.Callable;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class CacheInterceptorTest {

	private ObjectFactory factory;

	@BeforeEach
	void setup()
	{
		this.factory = ObjectFactory.newInstance();
	}

	@Test
	void testCaching_noArgs()
	{
		CachableNoArgs cachable = this.factory.requestExact(CachableNoArgs.class);
		Verify.that(cachable::call).suppliesNonUniqueValues();
	}

	@Test
	void testCaching_withArguments()
	{
		CachableWithArgs cachable = this.factory.requestExact(CachableWithArgs.class);
		Object key = new Object();
		Verify.that(() -> cachable.apply(key)).suppliesNonUniqueValues();
		Verify.that(cachable.apply(1)).isNotSameAs(cachable.apply(2));
	}

	public static class CachableNoArgs implements Callable<Object>
	{
		@Override
		@Cache
		public Object call()
		{
			return new Object();
		}
	}

	public static class CachableWithArgs implements Function<Object, Object>
	{
		@Override
		@Cache
		public Object apply(Object object)
		{
			return new Object();
		}
	}

}