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
	void testCheck_nullValue()
	{
		Verify.that(() -> Constraints.validate(null)).doesThrow(NullPointerException.class);
	}

	@Test
	void testCheck_valueIsNotNull()
	{
		TestClass test = new TestClass();
		test.setFoo("");
		Verify.that(() -> Constraints.validate(test)).runsWithoutExceptions();
	}

	@Test
	void testCheck_valueIsNull()
	{
		TestClass test = new TestClass();
		Verify.that(() -> Constraints.validate(test)).doesThrow(ConstraintException.class);
	}

	@Test
	void testCheck_numberLessThan3()
	{
		NumberClass numberInstance = new NumberClass(2);
		Verify.that(() -> Constraints.validate(numberInstance)).doesThrow(ConstraintException.class);
	}

	@Test
	void testCheck_numberGreaterThan3()
	{
		NumberClass numberInstance = new NumberClass(4);
		Verify.that(() -> Constraints.validate(numberInstance)).runsWithoutExceptions();
	}

	@Test
	void testCheck_incorrectConstraint_throwsException()
	{
		IncorrectType incorrectType = new IncorrectType();
		Verify.that(() -> Constraints.validate(incorrectType)).doesThrow(ConstraintTypeMismatchException.class);
	}

	@Test
	void testCheck_beanGetter_whenNull()
	{
		BeanCheck bean = Beans.create(BeanCheck.class);
		Verify.that(() -> Constraints.validate(bean)).doesThrow(ConstraintException.class);
	}

	@Test
	void testCheck_beanGetter_whenNonNull()
	{
		BeanCheck bean = Beans.create(BeanCheck.class);
		bean.setString("");
		Verify.that(() -> Constraints.validate(bean)).runsWithoutExceptions();
	}

	@Test
	void testCheck_errorMessage_isEqual()
	{
		BeanCheck bean = Beans.create(BeanCheck.class);
		try
		{
			Constraints.validate(bean);
		}
		catch (ConstraintException exception)
		{
			Verify.that(exception.getMessage().equals("Must not be null"));
		}
	}

	@Test
	void testCheck_errorMessage_default()
	{
		NumberClass number = new NumberClass(0);
		try
		{
			Constraints.validate(number);
		}
		catch (ConstraintException exception)
		{
			Verify.that(exception.getMessage().equals("Validation failed"));
		}
	}

	@Test
	void testGetConstraint_nonConstraint()
	{
		@NotAConstraint class Foo {}
		NotAConstraint annotation = Foo.class.getDeclaredAnnotation(NotAConstraint.class);

		Verify.that(() -> Constraints.getConstraint(annotation)).doesThrow(IllegalArgumentException.class);
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
	@Constraint(validator = NotNullValidator.class)
	public @interface NotNull
	{
		String error() default "Must not be null";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.METHOD})
	@Constraint(validator = GreaterThan3Validator.class)
	public @interface GreaterThan3
	{

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public @interface NotAConstraint
	{

	}



	public static class NotNullValidator implements ConstraintValidator<Object>
	{

		@Override
		public void check(Object object) throws ConstraintException
		{
			if (object == null)
			{
				throw new ConstraintException(this);
			}
		}

		@Override
		public Class<Object> validationType()
		{
			return Object.class;
		}

	}

	public static class GreaterThan3Validator implements ConstraintValidator<Number>
	{

		@Override
		public void check(Number number) throws ConstraintException
		{
			if (number.longValue() <= 3)
			{
				throw new ConstraintException(this);
			}
		}

		@Override
		public Class<Number> validationType()
		{
			return Number.class;
		}

	}

}
