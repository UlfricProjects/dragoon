package com.ulfric.dragoon.extension.intercept.sla;

import java.lang.reflect.Executable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.extension.intercept.Interceptor;

public class SLAInterceptor extends Interceptor<SLA> {

	private final long sla;
	private final String callDescription;
	private final String slaDescription;

	@Inject
	private Logger logger;

	public SLAInterceptor(Executable call, SLA declaration) {
		super(call, declaration);

		this.callDescription = call.toGenericString();
		this.sla = declaration.unit().toNanos(declaration.value());
		this.slaDescription = formatNanos(sla);
	}

	@Override
	public Object invoke(Object[] arguments, Callable<?> proceed) throws Exception {
		long start = System.nanoTime();
		try {
			return proceed.call();
		} finally {
			long took = System.nanoTime() - start;

			if (took > sla) {
				String message = String.format("%s violated it's SLA of %s by taking %s",
						callDescription, slaDescription, formatNanos(took));
				logger.warning(message);
			}
		}
	}

	private String formatNanos(long nanos) {
		long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
		if (millis == 0) {
			return nanos + " nanoseconds";
		}

		return millis + " milliseconds";
	}

}
