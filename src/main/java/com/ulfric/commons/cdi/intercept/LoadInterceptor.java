package com.ulfric.commons.cdi.intercept;

import com.ulfric.commons.text.FormatUtils;

public class LoadInterceptor extends StateInterceptor {

	@Override
	protected void before(String name)
	{
		this.logger.info("Loading " + name);
	}

	@Override
	protected void after(String name, long timeToProcessInMillis)
	{
		this.logger.info("Loaded " + name + " in " + FormatUtils.formatMilliseconds(timeToProcessInMillis));
	}

}