package com.ulfric.dragoon;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.scope.ScopeNotPresentException;
import com.ulfric.dragoon.scope.ScopeStrategy;
import com.ulfric.dragoon.scope.Scoped;
import com.ulfric.dragoon.scope.Shared;
import com.ulfric.dragoon.scope.SharedScopeStrategy;
import com.ulfric.dragoon.scope.SuppliedScopeStrategy;
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
		Verify.that(() -> this.scopes.getScopedObject(Example.class).read()).doesThrow(NoSuchElementException.class);
	}

	@Test
	void testGetScopedObject_fromParent()
	{
		this.scopes.registerBinding(Shared.class, SharedScopeStrategy.class);
		SharedScopeStrategy strategy = (SharedScopeStrategy) this.scopes.getRegisteredBinding(Shared.class);
		Verify.that(() -> strategy.getOrCreate(Example.class).read()).suppliesNonUniqueValues();
		Scopes scopes = this.scopes.createChild();
		scopes.registerBinding(Shared.class, SharedScopeStrategy.class);
		Verify.that(scopes.getScopedObject(Example.class).read()).isSameAs(strategy.getOrEmpty(Example.class).read());
	}

	@Test
	public void testGetScopeParent()
	{
		this.scopes.registerBinding(Shared.class, SharedScopeStrategy.class);
		SharedScopeStrategy strategy = (SharedScopeStrategy) this.scopes.getRegisteredBinding(Shared.class);
		Verify.that(() -> strategy.getOrCreate(Example.class).read()).suppliesNonUniqueValues();
		Scopes scopes = this.scopes.createChild();
		Example read = scopes.createChild().getScope(Shared.class).getOrEmpty(Example.class).read();
		Verify.that(read).isSameAs(strategy.getOrEmpty(Example.class).read());
	}

	@Test
	public void testGetScopedObject_resolveEmpty()
	{
		this.scopes.registerBinding(Shared.class, SuppliedScopeStrategy.class);
		SuppliedScopeStrategy pool = (SuppliedScopeStrategy) this.scopes.getRegisteredBinding(Shared.class);
		Verify.that(pool.getOrEmpty(Example.class).isEmpty()).isTrue();
	}

	@Test
	void testGetScopedObject_fromParentSupplied()
	{
		this.scopes.registerBinding(Shared.class, SuppliedScopeStrategy.class);
		SuppliedScopeStrategy pool = (SuppliedScopeStrategy) this.scopes.getRegisteredBinding(Shared.class);
		pool.register(Example.class, Example::new);
		Scopes scopes = this.scopes.createChild();
		scopes.registerBinding(Shared.class, SuppliedScopeStrategy.class);
		Verify.that(scopes.getScopedObject(Example.class).read()).isNotNull();
	}

	@Test
	void testGetScopedObject_nonRegisteredNoParent()
	{
		this.scopes.registerBinding(Shared.class, EmptyStrategy.class);
		Verify.that(() -> this.scopes.getScopedObject(Example.class).read()).doesThrow(NoSuchElementException.class);
	}

	@Test
	void testGetScopeStrategy_fromParent()
	{
		this.scopes.registerBinding(Shared.class, SharedScopeStrategy.class);
		Scopes scopes = this.scopes.createChild();
		Verify.that(scopes.getScope(Shared.class)).isNotNull();
	}

	@Test
	void testGetScopedObject_fromSelfWithParent()
	{
		this.scopes.registerBinding(Shared.class, EmptyStrategy.class);

		Scopes scopes = this.scopes.createChild();

		scopes.registerBinding(Shared.class, SharedScopeStrategy.class);

		Verify.that(scopes.getScope(Shared.class)).isInstanceOf(SharedScopeStrategy.class);
	}

	@Test
	void testGetScope_unimplementedScope_returnsNull()
	{
		Verify.that(this.scopes.getScope(Empty.class)).isNull();
	}

	@Test
	void testGetScopedObject_unimplementedScope_throwsException()
	{
		Verify.that(() -> this.scopes.getScopedObject(Example.class).read()).doesThrow(ScopeNotPresentException.class);
	}

	@Test
	public void testScopesSetParent_nullConstruction() {
	}

	enum None {
	}

	@Shared
	static class Example
	{

	}

	@interface Empty
	{

	}


	static class RandomStrategy implements ScopeStrategy {

		@Override
		public <T> Scoped<T> getOrCreate(Class<T> request)
		{
			return null;
		}

		@Override
		public <T> Scoped<T> getOrEmpty(Class<T> request)
		{
			return null;
		}
	}

	static class EmptyStrategy implements ScopeStrategy {

		@Override
		public <T> Scoped<T> getOrCreate(Class<T> request)
		{
			return new Scoped<>(request, null);
		}

		@Override
		public <T> Scoped<T> getOrEmpty(Class<T> request) {
			return Scoped.createEmptyScope(request);
		}

	}

}