package com.ulfric.dragoon.exception;

public class Try {

	public static void to(CheckedRunnable runnable)
	{
		try
		{
			runnable.run();
		}
		catch (Throwable rethrow)
		{
			throw new RuntimeException(rethrow);
		}
	}

	@FunctionalInterface
	public interface CheckedRunnable
	{
		void run() throws Throwable;
	}

	private Try() { }

}