package com.ulfric.dragoon.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.bean.Bean;
import com.ulfric.commons.exception.Try;
import com.ulfric.testing.Util;
import com.ulfric.testing.UtilTestBase;
import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
@Util(Beans.class)
public class BeansTest extends UtilTestBase {

	@Test
	void testCreate_nonBean()
	{
		Verify.that(() -> Beans.create(NotABean.class)).doesThrow(Beans.BeanCreationException.class);
	}

	@Test
	void testCreate_extendsBean()
	{
		Verify.that(() -> Beans.create(ConcreteBean.class)).suppliesUniqueValues();
		Verify.that(Beans.create(ConcreteBean.class)).isExactType(ConcreteBean.class);
	}

	@Test
	void testCreate_interfaceBean_suppliesUnique()
	{
		Verify.that(() -> Beans.create(InterfaceBean.class)).suppliesUniqueValues();
	}

	@Test
	void testBean_setGetValues_wrapped()
	{
		InterfaceBean bean = Beans.create(InterfaceBean.class);

		Verify.that(() -> bean.setString("foo")).runsWithoutExceptions();
		Verify.that(bean::getString).valueIsEqualTo("foo");
	}

	@Test
	void testBean_setGetValues_primitive()
	{
		InterfaceBean bean = Beans.create(InterfaceBean.class);

		Verify.that(() -> bean.setTrue(true)).runsWithoutExceptions();
		Verify.that(bean::isTrue).valueIsEqualTo(true);
	}

	@Test
	void testCreate_interface_carriesTypeAnnotationsFromParent()
	{
		InterfaceBean bean = Beans.create(InterfaceBean.class);

		Verify.that(bean.getClass().isAnnotationPresent(TypeAnnotation.class)).isTrue();
	}

	@Test
	void testCreate_interface_carriesMethodAnnotationsFromParent()
	{
		InterfaceBean bean = Beans.create(InterfaceBean.class);

		Method method = Try.to(() -> bean.getClass().getDeclaredMethod("getString"));

		Verify.that(method.isAnnotationPresent(MethodAnnotation.class)).isTrue();
	}

	@Test
	void testCreate_reusesDynamicClass()
	{
		Verify.that(Beans.create(InterfaceBean.class)::getClass).suppliesNonUniqueValues();
	}

	@Test
	void testCreate_addsDynamicAnnotation()
	{
		InterfaceBean bean = Beans.create(InterfaceBean.class);

		Verify.that(InterfaceBean.class.isAnnotationPresent(DynamicBean.class)).isFalse();
		Verify.that(bean.getClass().isAnnotationPresent(DynamicBean.class)).isTrue();
	}

	public static class NotABean
	{

	}

	public static class ConcreteBean extends Bean<ConcreteBean>
	{

	}

	@TypeAnnotation
	public interface InterfaceBean
	{
		@MethodAnnotation
		String getString();

		void setString(String string);

		boolean isTrue();

		void setTrue(boolean bool);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface TypeAnnotation
	{

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.FIELD})
	public @interface MethodAnnotation
	{

	}

}
