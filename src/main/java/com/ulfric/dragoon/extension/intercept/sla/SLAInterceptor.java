package com.ulfric.dragoon.extension.intercept.sla;

import java.lang.reflect.Executable;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.Interceptor;

public class SLAInterceptor extends Interceptor<SLA> {

	private final long sla;
	private final String description;

	@Inject
	private Logger logger;

	public SLAInterceptor(Executable call, SLA declaration) {
		super(call, declaration);

		this.description = call.toGenericString();
		this.sla = declaration.unit().toMillis(declaration.value());
	}

	@Override
	public Object invoke(Object[] arguments, Callable<?> proceed) throws Exception {
		long start = System.currentTimeMillis();
		try {
			return proceed.call();
		} finally {
			long took = System.currentTimeMillis() - start;

			if (took > sla) {
				logger.warning(String.format("%s violated it's SLA of %dms by taking %dms", description, sla, took));
			}
		}
	}

}
