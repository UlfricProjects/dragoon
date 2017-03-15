package com.ulfric.dragoon.bean;

final class Getter {

	private final Class<?> type;
	private final String fieldName;

	Getter(String fieldName, Class<?> type)
	{
		this.fieldName = fieldName;
		this.type = type;
	}

	public Class<?> getType()
	{
		return this.type;
	}

	public String getFieldName()
	{
		return this.fieldName;
	}

}