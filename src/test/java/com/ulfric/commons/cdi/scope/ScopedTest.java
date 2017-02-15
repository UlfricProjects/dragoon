package com.ulfric.commons.cdi.scope;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ScopedTest {

	private Scoped<Object> scoped;

	@BeforeEach
	void init()
	{
		this.scoped = new Scoped<>(Object.class, new Object());
	}

	@Test
	void testReadChangesIsRead()
	{
		Verify.that(this.scoped.isRead()).isFalse();
		this.scoped.read();
		Verify.that(this.scoped.isRead()).isTrue();
	}
	
	@Test
	void testReadIsNotNull()
	{
		Verify.that(this.scoped.read()).isNotNull();
	}

}