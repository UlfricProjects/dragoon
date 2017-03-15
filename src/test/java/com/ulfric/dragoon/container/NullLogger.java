package com.ulfric.dragoon.container;

import java.util.logging.Logger;

public class NullLogger extends Logger {

	public NullLogger()
	{
		super(null, null);
	}

}