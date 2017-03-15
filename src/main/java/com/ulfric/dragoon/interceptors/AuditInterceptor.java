package com.ulfric.dragoon.interceptors;

import java.lang.reflect.Executable;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.intercept.Context;
import com.ulfric.dragoon.intercept.Interceptor;
import com.ulfric.commons.naming.Named;
import com.ulfric.commons.text.FormatUtils;

public final class AuditInterceptor implements Interceptor {

	@Inject
	private Logger logger;

	@Override
	public Object intercept(Context context)
	{
		return new AuditCall(context).call();
	}

	private final class AuditCall implements Callable<Object>
	{
		private final Context context;
		private final String name;
		private final String type;
		private final String beforeType;
		private final String afterType;

		private long time;
		private Object result;

		AuditCall(Context context)
		{
			this.context = context;
			this.name = this.getName();
			this.type = this.getAroundType();
			this.beforeType = this.qualifyBeforeType();
			this.afterType = this.qualifyAfterType();
		}

		private String getName()
		{
			Object object = this.context.getOwner();
			if (object instanceof Named)
			{
				return ((Named) object).getName();
			}

			return String.valueOf(object);
		}

		private String getAroundType()
		{
			Executable destination = this.context.getDestinationExecutable();
			Audit around = destination.getAnnotation(Audit.class);
			if (around == null)
			{
				return destination.getName();
			}
			return around.value();
		}

		private String qualifyBeforeType()
		{
			String qualifiedType = this.type;
			if (qualifiedType.endsWith("e"))
			{
				qualifiedType = qualifiedType.substring(0, qualifiedType.length() - 1);
			}
			qualifiedType = qualifiedType + "ing";
			return qualifiedType;
		}

		private String qualifyAfterType()
		{
			String qualifiedType = this.type;
			if (qualifiedType.endsWith("e"))
			{
				qualifiedType = qualifiedType + 'd';
			}
			else
			{
				qualifiedType = qualifiedType + "ed";
			}
			return qualifiedType;
		}

		@Override
		public Object call()
		{
			this.logBefore();
			this.timedProceed();
			this.logAfter();

			return this.result;
		}

		private void timedProceed()
		{
			this.time = System.currentTimeMillis();
			this.result = this.context.proceed();
			this.time = System.currentTimeMillis() - this.time;
		}

		private void logBefore()
		{
			AuditInterceptor.this.logger.info(this.beforeType + ' ' + this.name);
		}

		private void logAfter()
		{
			AuditInterceptor.this.logger.info(this.afterType + ' ' + this.name + " in " +
					FormatUtils.formatMilliseconds(this.time));
		}
	}

}