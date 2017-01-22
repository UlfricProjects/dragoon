package com.ulfric.commons.cdi.intercept;

import com.ulfric.commons.text.FormatUtils;

public class EnableInterceptor extends StateInterceptor {

	@Override
	protected void before(String name)
	{
		this.logger.info("Enabling " + name);
	}

	@Override
	protected void after(String name, long timeToProcessInMillis)
	{
		this.logger.info("Enabled " + name + " in " + FormatUtils.formatMilliseconds(timeToProcessInMillis));
	}

}