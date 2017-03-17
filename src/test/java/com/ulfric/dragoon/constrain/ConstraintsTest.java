package com.ulfric.dragoon.constrain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.bean.Beans;
import com.ulfric.testing.Util;
import com.ulfric.testing.UtilTestBase;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
@Util(Constraints.class)
public class ConstraintsTest extends UtilTestBase {

	@Test
	void testCheck_valueIsNotNull()
	{
		TestClass test = new TestClass();
		test.setFoo("");
		Verify.that(() -> Constraints.check(test)).runsWithoutExceptions();
	}

	@Test
	void testCheck_valueIsNull()
	{
		TestClass test = new TestClass();
		Verify.that(() -> Constraints.check(test)).doesThrow(ConstraintException.class);
	}

	@Test
	void testCheck_numberLessThan3()
	{
		NumberClass numberInstance = new NumberClass(2);
		Verify.that(() -> Constraints.check(numberInstance)).doesThrow(ConstraintException.class);
	}

	@Test
	void testCheck_numberGreaterThan3()
	{
		NumberClass numberInstance = new NumberClass(4);
		Verify.that(() -> Constraints.check(numberInstance)).runsWithoutExceptions();
	}

	@Test
	void testCheck_incorrectConstraint_throwsException()
	{
		IncorrectType incorrectType = new IncorrectType();
		Verify.that(() -> Constraints.check(incorrectType)).doesThrow(ConstraintTypeMismatchException.class);
	}

	@Test
	void testCheck_beanGetter_whenNull()
	{
		BeanCheck bean = Beans.create(BeanCheck.class);
		Verify.that(() -> Constraints.check(bean)).doesThrow(ConstraintException.class);
	}

	@Test
	void testCheck_beanGetter_whenNonNull()
	{
		BeanCheck bean = Beans.create(BeanCheck.class);
		bean.setString("");
		Verify.that(() -> Constraints.check(bean)).runsWithoutExceptions();
	}

	public static class TestClass
	{
		@NotNull private static final Object STATIC_NULL_IGNORED = null;

		@NotNull private String foo;
		private String instanceIgnored = null;

		public void setFoo(String foo)
		{
			this.foo = foo;
		}

	}

	public static class NumberClass
	{
		@GreaterThan3 private final int x;
		@NoOp private final int noOp = 2;

		public NumberClass(int x)
		{
			this.x = x;
		}
	}

	public static class IncorrectType
	{
		@GreaterThan3 private String foo;
	}

	public interface BeanCheck
	{
		void setString(String string);

		@NotNull
		String getString();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.METHOD})
	@Constraint(adapter = NotNullAdapter.class)
	public @interface NotNull
	{

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.METHOD})
	@Constraint(adapter = GreaterThan3Adapter.class)
	public @interface GreaterThan3
	{

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.METHOD})
	@Constraint
	public @interface NoOp
	{

	}

	public static class NotNullAdapter implements ConstraintAdapter<Object>
	{

		@Override
		public void check(Object object) throws ConstraintException
		{
			if (object == null)
			{
				throw new ConstraintException("Cannot be null");
			}
		}

		@Override
		public Class<Object> adaptionType()
		{
			return Object.class;
		}

	}

	public static class GreaterThan3Adapter implements ConstraintAdapter<Number>
	{

		@Override
		public void check(Number number) throws ConstraintException
		{
			if (number.longValue() <= 3)
			{
				throw new ConstraintException();
			}
		}

		@Override
		public Class<Number> adaptionType()
		{
			return Number.class;
		}
	}

}
