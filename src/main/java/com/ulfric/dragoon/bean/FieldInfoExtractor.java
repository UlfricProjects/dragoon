package com.ulfric.dragoon.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.ulfric.commons.bean.Bean;

public enum FieldInfoExtractor {

	;

	static FieldInfo from(Method method)
	{
		int sub = method.getName().startsWith("is") ? 2 : 3;

		String name = FieldInfoExtractor.getName(method, sub);
		Class<?> type = FieldInfoExtractor.getType(method);
		Annotation[] annotations = method.getAnnotations();

		return new FieldInfo(name, type, annotations);
	}

	private static String getName(Method method, int sub)
	{
		String name = method.getName();

		return name.substring(sub, sub + 1).toLowerCase() + name.substring(sub + 1);
	}

	private static Class<?> getType(Method method)
	{
		return method.getReturnType();
	}

	static class FieldInfo extends Bean
	{
		private final String fieldName;
		private final Class<?> fieldType;
		private final Annotation[] annotations;

		private FieldInfo(String fieldName, Class<?> fieldType, Annotation[] annotations)
		{
			this.fieldType = fieldType;
			this.fieldName = fieldName;
			this.annotations = annotations;
		}

		String getFieldName()
		{
			return this.fieldName;
		}

		Class<?> getFieldType()
		{
			return this.fieldType;
		}

		Annotation[] getAnnotations()
		{
			return this.annotations;
		}
	}

}
