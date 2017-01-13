package com.ulfric.commons.cdi.construct;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.construct.scope.Scope;
import com.ulfric.commons.cdi.construct.scope.ScopeStrategy;
import com.ulfric.commons.cdi.inject.Injector;
import com.ulfric.commons.cdi.intercept.Context;
import com.ulfric.commons.cdi.intercept.Intercept;
import com.ulfric.commons.cdi.intercept.Interceptor;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class BeanFactoryTest {

	private BeanFactory factory;

	@BeforeEach
	public void init()
	{
		this.factory = BeanFactory.newInstance();
	}

	@Test
	public void test_newInstance_returnsNewValues()
	{
		Verify.that(BeanFactory::newInstance).suppliesUniqueValues();
	}

	@Test
	public void test_getIntjector_returnsNonNull()
	{
		Verify.that(this.factory.getInjector()).isNotNull();
	}

	@Test
	public void test_bindScope_runsWithoutExceptions()
	{
		Verify.that(
				() -> this.factory.bind(ScopeAnnotation.class).toScope(ScopeStrategyTest.class)
		).runsWithoutExceptions();
	}

	@Test
	public void test_request_throwsWhenNull()
	{
		Verify.that(() -> this.factory.request(null)).doesThrow(NullPointerException.class);
	}

	@Test
	public void test_request_returnsBytebuddyInstance()
	{
		Verify.that(this.factory.request(FooClass.class)).isInstanceOf(FooClass.class);
	}

	@Test
	public void test_request_returnsCorrectScope()
	{
		this.factory.bind(ScopeAnnotation.class).toScope(ScopeStrategyTest.class);

		Verify.that(((ScopedClass) this.factory.request(ScopedClass.class)).hasObject());
	}

	@Test
	public void test_bind_throwsWhenNull()
	{
		Verify.that(() -> this.factory.bind(null)).doesThrow(NullPointerException.class);
	}

	@Test
	public void test_bind_returnsNonNull()
	{
		Binding<FooClass> binding = this.factory.bind(FooClass.class);

		Verify.that(binding).isNotNull();
	}

	@Test
	public void test_createInstance_throwsScopeNotPresent()
	{
		Method createInstance = MethodUtils.getMatchingMethod(
				BeanFactory.class, "createInstance", Annotation.class, Class.class
		);

		createInstance.setAccessible(true);

		Verify.that(() ->
		{
			createInstance.invoke(
					this.factory, ScopedClass.class.getDeclaredAnnotation(UnusedScope.class), null
			);
		}).doesThrow(InvocationTargetException.class);
	}

	@Test
	public void testRequest_BeanFactory_isNotNull()
	{
		Verify.that(this.factory.request(BeanFactory.class)).isNotNull();
	}

	@Test
	public void testRequest_BeanFactory_isUnique()
	{
		Verify.that(() -> this.factory.request(BeanFactory.class)).suppliesUniqueValues();
	}

	@Test
	public void test_createInterceptor_binding()
	{
		Verify.that(() ->
		{
			this.factory.bind(Baz.class).toInterceptor(BazInterceptor.class);
			this.factory.bind(Baz2.class).toInterceptor(Baz2Interceptor.class);
			this.factory.request(Baz2Intercepted.class);
		}).runsWithoutExceptions();
	}

	@Test
	public void test_createInterceptor_wrongImplType()
	{
		Verify.that(() ->
		{
			this.factory.bind(NoBaz.class).to(NoBazBinding.class);
			this.factory.request(BazNotIntercepted.class);
		}).doesThrow(RuntimeException.class);
	}

	@Test
	public void test_beanFactory_parentScopeLookup()
	{
		BeanFactory child = this.factory.createChild();

		this.factory.bind(ScopeAnnotation.class).toScope(ScopeStrategyTest.class);

		Verify.that((child.requestExact(ScopedClass.class)).hasObject()).isTrue();
	}

	@Test
	public void test_beanFactory_parentInterceptor()
	{
		BeanFactory child = this.factory.createChild();

		this.factory.bind(Baz.class).toInterceptor(BazInterceptor.class);

		BazIntercepted intercepted = child.requestExact(BazIntercepted.class);

		Verify.that(intercepted::foo).runsWithoutExceptions();
	}

	@Test
	public void test_beanFactory_requestExact()
	{
		this.factory.bind(Baz.class).toInterceptor(BazInterceptor.class);

		Verify.that(() -> this.factory.requestExact(Baz.class)).doesThrow(IllegalRequestExactException.class);
	}

	@Test
	public void test_beanFactory_createInterceptorClassNoImpl()
	{
		Method method = MethodUtils.getMatchingMethod(BeanFactory.class, "createInterceptableClass", Class.class);
		method.setAccessible(true);

		Verify.that(() -> method.invoke(this.factory, FooEnum.class)).valueIsEqualTo(FooEnum.class);
	}

	public static class FooClass
	{

	}

	public interface FooInterface
	{

	}

	public enum FooEnum
	{

	}

	public static abstract class FooAbstractClass
	{

	}

	public static final class FooFinalClass
	{

	}

	@UnusedScope
	@ScopeAnnotation
	public static class ScopedClass
	{

		private Object object = null;

		public ScopedClass()
		{

		}

		public ScopedClass(Object object)
		{
			this.object = object;
		}

		public boolean hasObject()
		{
			return this.object != null;
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Scope
	public @interface ScopeAnnotation
	{

	}

	public static class ScopeStrategyTest implements ScopeStrategy<ScopeAnnotation>
	{

		@Override
		public <T> T getInstance(Class<T> request, ScopeAnnotation scope, Injector injector)
		{
			try
			{
				return request.getDeclaredConstructor(Object.class).newInstance(new Object());
			}
			catch (InstantiationException | IllegalAccessException |
					NoSuchMethodException | InvocationTargetException e)
			{
				throw new RuntimeException(e);
			}
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Scope
	public @interface UnusedScope
	{

	}

	public static class UnusedScopeStrategy implements ScopeStrategy<UnusedScope>
	{

		@Override
		public <T> T getInstance(Class<T> request, UnusedScope scope, Injector injector)
		{
			try
			{
				return request.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}

	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Intercept
	@Inherited
	public @interface Baz {

	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Intercept
	public @interface Baz2 {

	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Intercept
	public @interface NoBaz {

	}

	public static class BazInterceptor implements Interceptor
	{

		@Override
		public Object intercept(Context context)
		{
			return context.proceed();
		}

	}

	public static class Baz2Interceptor implements Interceptor
	{

		@Override
		public Object intercept(Context context)
		{
			return context.proceed();
		}

	}

	public static class NoBazBinding implements NoBaz
	{

		@Override
		public Class<? extends Annotation> annotationType()
		{
			return NoBaz.class;
		}

	}

	public static class BazIntercepted
	{

		@Baz
		public void foo()
		{

		}

	}

	public static class Baz2Intercepted extends BazIntercepted
	{

		@Baz2
		public void foo2()
		{

		}

	}

	public static class BazNotIntercepted extends BazIntercepted
	{

		@NoBaz
		public void bar()
		{

		}

	}

}
