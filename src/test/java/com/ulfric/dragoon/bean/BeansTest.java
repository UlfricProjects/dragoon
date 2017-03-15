package com.ulfric.dragoon.bean;

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
	public void testCreate_nonBean()
	{
		Verify.that(() -> Beans.create(NotABean.class)).doesThrow(Beans.BeanCreationException.class);
	}

	@Test
	public void testCreate_extendsBean()
	{
		Verify.that(() -> Beans.create(ConcreteBean.class)).suppliesUniqueValues();
		Verify.that(Beans.create(ConcreteBean.class)).isExactType(ConcreteBean.class);
	}

	@Test
	public void testCreate_interfaceBean()
	{
		Verify.that(() -> Beans.create(InterfaceBean.class)).suppliesUniqueValues();
		InterfaceBean bean = Beans.create(InterfaceBean.class);

		Verify.that(() -> bean.setString("foo")).runsWithoutExceptions();
		Verify.that(bean::getString).valueIsEqualTo("foo");

		Verify.that(bean::notAGetter).doesThrow(AbstractMethodError.class);
		Verify.that(bean::getKiddingItsVoid).doesThrow(AbstractMethodError.class);
		Verify.that(() -> bean.getWaitIsThisASetter(null)).doesThrow(AbstractMethodError.class);
	}

	public static class NotABean
	{

	}

	public static class ConcreteBean extends Bean<ConcreteBean>
	{

	}

	public interface InterfaceBean
	{
		String getString();

		void setString(String string);

		Object notAGetter();

		void getKiddingItsVoid();

		Object getWaitIsThisASetter(Object object);
	}

}
