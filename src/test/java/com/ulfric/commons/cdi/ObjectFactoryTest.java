package com.ulfric.commons.cdi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Default;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ObjectFactoryTest {

	private ObjectFactory factory;

	@BeforeEach
	void init()
	{
		this.factory = ObjectFactory.newInstance();
	}

	@Test
	void testNewInstance()
	{
		Verify.that(ObjectFactory::newInstance).suppliesUniqueValues();
	}

	@Test
	void testHasParent_root_isFalse()
	{
		Verify.that(this.factory.hasParent()).isFalse();
	}

	@Test
	void testBind_null_throwsNPE()
	{
		Verify.that(() -> this.factory.bind(null)).doesThrow(NullPointerException.class);
	}

	@Test
	void testBind_nonnull_isNotNull()
	{
		Verify.that(this.factory.bind(Hello.class)).isNotNull();
	}

	@Test
	void testBindScope_null_throwsNPE()
	{
		Verify.that(() -> this.factory.bindScope(null)).doesThrow(NullPointerException.class);
	}

	@Test
	void testBindScope_nonnull_isNotNull()
	{
		Verify.that(this.factory.bindScope(Default.class)).isNotNull();
	}

	@Test
	void testRequest_null_throwsNPE()
	{
		Verify.that(() -> this.factory.request(null)).doesThrow(NullPointerException.class);
	}

	@Test
	void testRequest_nonnullButEmpty_null()
	{
		Verify.that(this.factory.request(Hello.class)).isNull();
	}

	@Test
	void testRequest_nonnull_nonnull()
	{
		this.factory.bind(Hello.class).to(HelloImpl.class);
		Verify.that(this.factory.request(Hello.class)).isNotNull();
	}

	@Test
	void testRequest_instantiableNonnullButEmpty_nonnull()
	{
		Verify.that(this.factory.request(HelloImpl.class)).isNotNull();
	}

	@Test
	void testRequest_child()
	{
		this.factory.bind(Hello.class).to(HelloImpl.class);
		Verify.that(this.factory.createChild().request(Hello.class)).isNotNull();
	}

	@Test
	void testRequest_doesInjection()
	{
		this.factory.bind(Hello.class).to(HelloImpl.class);
		HelloInject hello = (HelloInject) this.factory.request(HelloInject.class);
		Verify.that(hello.inject).isNotNull();
	}

	@Test
	void testRequestExact_correctInstance()
	{
		Verify.that(this.factory.requestExact(HelloImpl.class)).isInstanceOf(HelloImpl.class);
	}

	@Test
	void testRequestExact_incorrectInstance()
	{
		this.factory.bind(Hello.class).to(HelloSibling.class);
		Verify.that(() -> this.factory.requestExact(Hello.class)).doesThrow(IllegalStateException.class);
	}

	interface Hello
	{
		
	}

	static class HelloImpl implements Hello
	{
		
	}

	static class HelloSibling
	{

	}

	static class HelloInject
	{
		@Inject
		Hello inject;
	}

}