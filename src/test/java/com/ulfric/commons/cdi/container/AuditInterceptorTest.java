package com.ulfric.commons.cdi.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.commons.cdi.interceptors.Audit;
import com.ulfric.commons.cdi.interceptors.AuditInterceptor;

@RunWith(JUnitPlatform.class)
public class AuditInterceptorTest {

	@Mock
	private Object owner;
	@Mock
	private Iterable<Interceptor> pipeline;
	@Mock
	private Callable<?> finalDestination;
	@Mock
	private Executable destinationExecutable;
	@Mock
	private Iterator<Interceptor> pipelineIterator;
	@Mock
	private Logger logger;

	private Interceptor interceptor;
	private String type;
	private String beforeType;
	private String afterType;

	private Object[] arguments = new Object[0];
	private Context context;

	@BeforeEach
	void init() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(this.pipeline.iterator()).thenReturn(this.pipelineIterator);
		this.context = Context.createInvocation(this.owner, this.pipeline,
				this.finalDestination, this.destinationExecutable, this.arguments);

		this.type = "Test";
		this.beforeType = "Testing";
		this.afterType = "Tested";
		Mockito.when(this.destinationExecutable.getName()).thenReturn(this.type);

		this.interceptor = new AuditInterceptor();
		Field loggerField = this.interceptor.getClass().getDeclaredField("logger");
		loggerField.setAccessible(true);
		loggerField.set(this.interceptor, this.logger);
	}

	@Test
	void testIntercept_logsAsExpected()
	{
		this.interceptor.intercept(this.context);
		this.verifyLoggers();
	}

	@Test
	void testIntercept_FiguresOutQualifiedTypes()
	{
		this.type = "Teste";
		Mockito.when(this.destinationExecutable.getName()).thenReturn(this.type);
		this.interceptor.intercept(this.context);
		this.verifyLoggers();
	}

	@Test
	void testIntercept_getsNameFromAnnotation()
	{
		Audit audit = new Audit()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return Audit.class;
			}

			@Override
			public String value()
			{
				return AuditInterceptorTest.this.type;
			}
		};
		Mockito.when(this.destinationExecutable.getAnnotation(Audit.class)).thenReturn(audit);

		this.type = "Hello";
		this.beforeType = "Helloing";
		this.afterType = "Helloed";

		this.interceptor.intercept(this.context);
		this.verifyLoggers();
	}

	private void verifyLoggers()
	{
		Mockito.verify(this.logger, Mockito.atLeastOnce())
			.info(this.beforeType + ' ' + this.owner);

		Mockito.verify(this.logger, Mockito.atLeastOnce())
			.info(Matchers.matches(Pattern.quote(this.afterType + ' ' + this.owner + " in ") + "[0-9]+ms"));
	}

}
