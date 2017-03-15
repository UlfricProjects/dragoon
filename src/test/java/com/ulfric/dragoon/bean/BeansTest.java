package com.ulfric.dragoon.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.bean.Bean;
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
	void testCreate_interfaceBean()
	{
		Verify.that(() -> Beans.create(InterfaceBean.class)).suppliesUniqueValues();
		InterfaceBean bean = Beans.create(InterfaceBean.class);

		Verify.that(() -> bean.setString("foo")).runsWithoutExceptions();
		Verify.that(bean::getString).valueIsEqualTo("foo");

		Verify.that(bean::notAGetter).doesThrow(AbstractMethodError.class);
		Verify.that(bean::getKiddingItsVoid).doesThrow(AbstractMethodError.class);
		Verify.that(() -> bean.getWaitIsThisASetter(null)).doesThrow(AbstractMethodError.class);

		Verify.that(Beans.create(InterfaceBean.class).getClass()).isEqualTo(bean.getClass());
	}

	@Test
	void testCreate_interface_carriesAnnotationsFromParent()
	{
		InterfaceBean bean = Beans.create(InterfaceBean.class);

		Verify.that(bean.getClass().isAnnotationPresent(FooAnnotation.class)).isTrue();
	}

	public static class NotABean
	{

	}

	public static class ConcreteBean extends Bean<ConcreteBean>
	{

	}

	@FooAnnotation
	public interface InterfaceBean
	{
		String getString();

		void setString(String string);

		Object notAGetter();

		void getKiddingItsVoid();

		Object getWaitIsThisASetter(Object object);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface FooAnnotation
	{

	}

}
