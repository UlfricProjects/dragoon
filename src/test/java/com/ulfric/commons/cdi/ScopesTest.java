package com.ulfric.commons.cdi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.scope.ScopeNotPresentException;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.cdi.scope.SharedScopeStrategy;
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

	@Test
	void testGetScopedObject_shared()
	{
		this.scopes.registerBinding(Shared.class, SharedScopeStrategy.class);
		Verify.that(() -> this.scopes.getScopedObject(Example.class).read()).suppliesNonUniqueValues();
	}

	@Test
	void testGetScopedObject_unimplementedScope_throwsException()
	{
		Verify.that(() -> this.scopes.getScopedObject(Example.class).read()).doesThrow(ScopeNotPresentException.class);
	}

	@Shared
	static class Example
	{
		
	}

}