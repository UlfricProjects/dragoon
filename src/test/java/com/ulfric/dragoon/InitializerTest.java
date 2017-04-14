package com.ulfric.dragoon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.scope.Scoped;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class InitializerTest {

	private ObjectFactory factory;
	private Initializer initializer;

	@BeforeEach
	void init()
	{
		this.factory = TestObjectFactory.newInstance();
		this.initializer = new Initializer();
	}

	@Test
	void testInitializeScoped_doNotInit()
	{
		DoNotInit init = new DoNotInit();
		Scoped<?> scoped = new Scoped<>(DoNotInit.class, init);
		Verify.that(() -> this.doInitialize(scoped)).runsWithoutExceptions();
		Verify.that(init.initialized).isFalse();
	}

	@Test
	void testInitializeScoped_doInit()
	{
		DoInit init = new DoInit();
		Scoped<?> scoped = new Scoped<>(DoInit.class, init);
		Verify.that(() -> this.doInitialize(scoped)).runsWithoutExceptions();
		Verify.that(init.initialized).isTrue();
	}

	@Test
	void testInitializeScoped_doInitPublic()
	{
		DoInitPublic init = new DoInitPublic();
		Scoped<?> scoped = new Scoped<>(DoInitPublic.class, init);
		Verify.that(() -> this.doInitialize(scoped)).runsWithoutExceptions();
		Verify.that(init.initialized).isTrue();
	}

	@Test
	void testInitializeScoped_doInitPrivate()
	{
		DoInitPrivate init = new DoInitPrivate();
		Scoped<?> scoped = new Scoped<>(DoInitPrivate.class, init);
		Verify.that(() -> this.doInitialize(scoped)).runsWithoutExceptions();
		Verify.that(init.initialized).isTrue();
	}

	@Test
	void testInitializeScoped_doNInitButAlreadyRead()
	{
		DoInit init = new DoInit();
		Scoped<?> scoped = new Scoped<>(DoInit.class, init);
		scoped.read("init");
		Verify.that(() -> this.doInitialize(scoped)).runsWithoutExceptions();
		Verify.that(init.initialized).isFalse();
	}

	@Test
	void testInitializeScoped_canReadInjectables_noExceptions()
	{
		Verify.that(() -> this.factory.requestExact(CanReadInjectables.class)).runsWithoutExceptions();
	}

	private void doInitialize(Scoped<?> scoped)
	{
		this.initializer.getInitializers(scoped).forEachRemaining(Runnable::run);
	}

	public static class DoNotInit
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

	public static class DoInitPublic
	{
		private boolean initialized = false;

		@Initialize
		public void init()
		{
			this.initialized = true;
		}

		public boolean isInitialized()
		{
			return this.initialized;
		}
	}

	public static class DoInitPrivate
	{
		private boolean initialized = false;

		@Initialize
		private void init()
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