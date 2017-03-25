package com.ulfric.dragoon.bean;

import java.lang.reflect.Method;

import com.ulfric.commons.bean.Bean;

public enum FieldInfoExtractor {

	;

	static FieldInfo from(Method method)
	{
		int sub = method.getName().startsWith("is") ? 2 : 3;

		String name = FieldInfoExtractor.getName(method, sub);
		Class<?> type = FieldInfoExtractor.getType(method);

		return new FieldInfo(name, type);
	}

	private static String getName(Method method, int sub)
	{
		String name = method.getName();

		return name.substring(sub, sub + 1).toLowerCase() + name.substring(sub + 1);
	}

	private static Class<?> getType(Method method)
	{
		return method.getReturnType() != Void.TYPE ? method.getReturnType() : method.getParameterTypes()[0];
	}

	static class FieldInfo extends Bean
	{
		private final String fieldName;
		private final Class<?> fieldType;

		private FieldInfo(String fieldName, Class<?> fieldType)
		{
			this.fieldType = fieldType;
			this.fieldName = fieldName;
		}

		String getFieldName()
		{
			return this.fieldName;
		}

		Class<?> getFieldType()
		{
			return this.fieldType;
		}
	}

}
