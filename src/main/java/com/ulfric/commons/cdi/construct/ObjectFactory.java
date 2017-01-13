package com.ulfric.commons.cdi.construct;

public class ObjectFactory {

	public static ObjectFactory newInstance()
	{
		return new ObjectFactory();
	}

	private ObjectFactory()
	{
		
	}

	ObjectFactory subfactory()
	{
		return new ObjectFactory();
	}

	

}