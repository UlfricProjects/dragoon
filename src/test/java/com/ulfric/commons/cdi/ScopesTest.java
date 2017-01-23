package com.ulfric.commons.cdi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.scope.ScopeNotPresentException;
import com.ulfric.commons.cdi.scope.ScopeStrategy;
import com.ulfric.commons.cdi.scope.Scoped;
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
	void testGetScopedObject_fromParent()
	{
		this.scopes.registerBinding(Shared.class, SharedScopeStrategy.class);
		Scopes scopes = this.scopes.createChild();
		Verify.that(scopes.getScope(Shared.class)).isNotNull();
	}

	@Test
	void testGetScopedObject_fromSelfWithParent()
	{
		this.scopes.registerBinding(Shared.class, RandomStrategy.class);

		Scopes scopes = this.scopes.createChild();

		scopes.registerBinding(Shared.class, SharedScopeStrategy.class);

		Verify.that(scopes.getScope(Shared.class)).isInstanceOf(SharedScopeStrategy.class);
	}

	@Test
	void testGetScope_unimplementedScope_returnsNull()
	{
		Verify.that(this.scopes.getScope(Random.class)).isNull();
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

	@interface Random
	{

	}

	static class RandomStrategy implements ScopeStrategy
	{

		@Override
		public <T> Scoped<T> getOrCreate(Class<T> request)
		{
			return null;
		}

	}

}