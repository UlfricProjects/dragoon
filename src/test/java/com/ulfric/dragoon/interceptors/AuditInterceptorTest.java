package com.ulfric.dragoon.interceptors;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.container.Container;
import com.ulfric.dragoon.container.NullLogger;
import com.ulfric.dragoon.scope.Supplied;
import com.ulfric.dragoon.scope.SuppliedScopeStrategy;

@RunWith(JUnitPlatform.class)
public class AuditInterceptorTest {

	private ObjectFactory factory;
	private Runnable intercepted;

	@Spy
	private NoLogger logger;

	@BeforeEach
	void init()
	{
		MockitoAnnotations.initMocks(this);
		this.factory = ObjectFactory.newInstance();

		SuppliedScopeStrategy scope = (SuppliedScopeStrategy) this.factory.request(Supplied.class);
		scope.register(NoLogger.class, () -> this.logger);
		this.factory.bind(Logger.class).to(NoLogger.class);

		this.intercepted = this.factory.requestExact(AuditMe.class);
	}

	@Test
	void testIntercept_logsAsExpected()
	{
		this.intercepted.run();
		this.verifyLoggers();
	}

	@Test
	void testIntercept_FiguresOutQualifiedTypes()
	{
		this.intercepted = this.factory.requestExact(SpecialCaseNameAuditMe.class);
		this.intercepted.run();
		this.verifyLoggers();
	}

	@Test
	void testIntercept_getsNameFromAnnotation()
	{
		this.intercepted = this.factory.requestExact(HelloAuditMe.class);
		this.intercepted.run();
		this.verifyLoggers("Helloing", "Helloed");
	}

	@Test
	void testIntercept_container()
	{
		this.intercepted = this.factory.requestExact(AuditMeContainer.class);
		this.intercepted.run();
		System.out.println(this.intercepted);
		this.verifyLoggers("Loading", "Loaded");
	}

	private void verifyLoggers()
	{
		this.verifyLoggers("Testing", "Tested");
	}

	private void verifyLoggers(String beforeType, String afterType)
	{
		Mockito.verify(this.logger, Mockito.times(1))
			.info(beforeType + ' ' + this.intercepted);

		Mockito.verify(this.logger, Mockito.times(1))
			.info(Matchers.matches(Pattern.quote(afterType + ' ' + this.intercepted + " in ") + "[0-9]+ms"));
	}

	static class AuditMe implements Runnable
	{
		@Audit("Test")
		@Override
		public void run() { }
	}

	static class ExtendsAuditMe extends AuditMe
	{
		@Override
		public void run() { }
	}

	static class SpecialCaseNameAuditMe implements Runnable
	{
		@Audit("Teste")
		@Override
		public void run() { }
	}

	static class HelloAuditMe implements Runnable
	{
		@Audit("Hello")
		@Override
		public void run() { }
	}

	static class AuditMeContainer extends Container implements Runnable
	{
		@Override
		public void run()
		{
			this.onLoad();
		}

		@Override
		public void onLoad()
		{
			
		}
	}

	@Supplied
	static class NoLogger extends NullLogger
	{

	}

}