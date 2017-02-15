package com.ulfric.commons.cdi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.initialize.Initialize;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Scoped;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class InitializerTest {

	private ObjectFactory factory;
	private Initializer initializer;

	@BeforeEach
	void init()
	{
		this.factory = ObjectFactory.newInstance();
		this.initializer = new Initializer();
	}

	@Test
	void testInitializeScoped_emptyScope()
	{
		Scoped<?> scoped = new Scoped<>(null, null);
		Verify.that(() -> this.initializer.initializeScoped(scoped)).doesThrow(IllegalStateException.class);
	}

	@Test
	void testInitializeScoped_dontInit()
	{
		DontInit init = new DontInit();
		Scoped<?> scoped = new Scoped<>(DontInit.class, init);
		Verify.that(() -> this.initializer.initializeScoped(scoped)).runsWithoutExceptions();
		Verify.that(init.initialized).isFalse();
	}

	@Test
	void testInitializeScoped_doInit()
	{
		DoInit init = new DoInit();
		Scoped<?> scoped = new Scoped<>(DoInit.class, init);
		Verify.that(() -> this.initializer.initializeScoped(scoped)).runsWithoutExceptions();
		Verify.that(init.initialized).isTrue();
	}

	@Test
	void testInitializeScoped_canReadInjectables_noExceptions()
	{
		Verify.that(() -> this.factory.requestExact(CanReadInjectables.class)).runsWithoutExceptions();
	}

	public static class DontInit
	{
		private boolean initialized = false;

		void init()
		{
			this.initialized = true;
		}

	}

	public static class DoInit
	{
		private boolean initialized = false;

		@Initialize
		void init()
		{
			this.initialized = true;
		}

		public boolean isInitialized()
		{
			return this.initialized;
		}
	}

	public static class CanReadInjectables
	{
		@Inject ObjectFactory factory;

		@Initialize
		void init()
		{
			Verify.that(this.factory).isNotNull();
		}

	}

}
