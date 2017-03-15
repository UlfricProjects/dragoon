package com.ulfric.dragoon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.intercept.Intercept;
import com.ulfric.dragoon.intercept.Interceptor;
import com.ulfric.dragoon.intercept.Context;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class SubclasserTest {

	private ObjectFactory factory;
	private Subclasser subclasser;

	@BeforeEach
	void init()
	{
		this.factory = ObjectFactory.newInstance();
		this.subclasser = new Subclasser(this.factory);
		this.factory.bind(InterceptMe.class).to(PassthroughInterceptor.class);
	}

	@Test
	void testCreateImplementationClass_concrete_nonnull()
	{
		Verify.that(this.subclasser.createImplementationClass(Hello.class)).isNotNull();
	}

	@Test
	void testCreateImplementationClass_interface_null()
	{
		Verify.that(this.subclasser.createImplementationClass(IHello.class)).isNull();
	}

	@Test
	void testCreateImplementationClass_abstract_null()
	{
		Verify.that(this.subclasser.createImplementationClass(AHello.class)).isNull();
	}

	@Test
	void testCreateImplementationClass_concreteNoInterceptors_sameAsParent()
	{
		Verify.that(this.subclasser.createImplementationClass(NoInterceptors.class)).isSameAs(NoInterceptors.class);
	}

	@Test
	void testCreateImplementationClass_concreteInterceptors_notSameAsParent()
	{
		Verify.that(this.subclasser.createImplementationClass(Hello.class)).isNotSameAs(Hello.class);
	}

	@Test
	void testCreateImplementationClass_concrete_extendedFromParent()
	{
		Verify.that(this.subclasser.createImplementationClass(Hello.class)).isAssignableTo(Hello.class);
	}

	@Test
	void testCreateImplementationClass_concrete_annotationsPresist()
	{
		Verify.that(this.subclasser.createImplementationClass(Hello.class).getAnnotations())
			.isEqualTo(Hello.class.getAnnotations());
	}

	@Test
	void testCreateImplementationClass_final_same()
	{
		Verify.that(this.subclasser.createImplementationClass(FHello.class)).isSameAs(FHello.class);
	}

	@Test
	void testCreateImplementationClass_primitive_same()
	{
		Class<?> primitive = int.class;
		Verify.that(this.subclasser.createImplementationClass(primitive)).isNull();
	}

	@Test
	void testCreateImplementationClass_array_same()
	{
		Class<?> array = Object[].class;
		Verify.that(this.subclasser.createImplementationClass(array)).isNull();
	}

	@Test
	void testCreateImplementationClass_interceptors_isIntercepted()
	{
		this.factory.bind(InterceptMe.class).to(GoodInterceptor.class);
		Verify.that(() -> this.subclasser.createImplementationClass(Bad.class).newInstance().bad())
				.valueIsEqualTo("good");
	}

	@Test
	void testCreateImplementationClass_interceptors_finalDestinationIsCalled()
	{
		Verify.that(() -> this.subclasser.createImplementationClass(Bad.class).newInstance().bad())
				.valueIsEqualTo("bad");
	}

	interface IHello
	{
		
	}

	static abstract class AHello implements IHello
	{
		
	}

	@AnnoHello
	static class Hello extends AHello
	{
		@InterceptMe
		public void interceptMe()
		{
			
		}
	}

	final static class FHello extends Hello
	{
		
	}

	static class NoInterceptors
	{
		
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@interface AnnoHello
	{
		
	}

	static class Bad
	{
		@InterceptMe
		public String bad()
		{
			return "bad";
		}
	}

	static class PassthroughInterceptor implements Interceptor
	{
		@Override
		public Object intercept(Context invocation)
		{
			return invocation.proceed();
		}
	}

	static class GoodInterceptor implements Interceptor
	{
		@Override
		public Object intercept(Context invocation)
		{
			return "good";
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Intercept
	@interface InterceptMe
	{
		
	}

}