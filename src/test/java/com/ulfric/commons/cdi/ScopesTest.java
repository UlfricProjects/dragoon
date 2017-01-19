package com.ulfric.commons.cdi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ScopesTest {

	private Scopes scopes;

	@BeforeEach
	void init()
	{
		this.scopes = new Scopes();
	}

	@Test
	void testNew_empty()
	{
		Verify.that(this.scopes).isNotNull();
	}

	@Test
	void testNew_child()
	{
		Verify.that(new Scopes(this.scopes)).isNotNull();
	}

	@Test
	void testRegisterBinding_notScopeStrategy_throwsIAE()
	{
		Verify.that(() -> this.scopes.registerBinding(null, Object.class)).doesThrow(IllegalArgumentException.class);
	}

	

}