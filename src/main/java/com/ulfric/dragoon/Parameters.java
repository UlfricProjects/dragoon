package com.ulfric.dragoon;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

import com.ulfric.dragoon.qualifier.EmptyQualifier;
import com.ulfric.dragoon.qualifier.Qualifier;

public final class Parameters {

	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public static final Parameters EMPTY =
			new Parameters(EmptyQualifier.INSTANCE, null, EMPTY_OBJECT_ARRAY);

	public static String getQualifiedName(Parameters parameters) {
		StringJoiner string = new StringJoiner(".");

		Object holder = parameters.getHolder();
		if (holder != null) {
			string.add(holder.toString());
		}

		string.add(parameters.getQualifier().getName());

		return string.toString();
	}

	public static Parameters unqualified(Object... arguments) {
		if (arguments == null || arguments.length == 0) {
			return EMPTY;
		}

		return new Parameters(EmptyQualifier.INSTANCE, null, arguments);
	}

	public static Parameters qualified(Qualifier qualifier) {
		if (qualifier == null) {
			return EMPTY;
		}

		return qualifiedHolder(qualifier, null);
	}

	public static Parameters qualifiedHolder(Qualifier qualifier, Object holder) {
		return new Parameters(qualifier, holder, EMPTY_OBJECT_ARRAY);
	}

	private final Qualifier qualifier;
	private final Object holder;
	private final Object[] arguments;

	public Parameters(Qualifier qualifier, Object holder, Object[] arguments) {
		Objects.requireNonNull(qualifier, "qualifier");
		Objects.requireNonNull(arguments, "arguments");

		this.qualifier = qualifier;
		this.holder = holder;
		this.arguments = arguments;
	}

	public Qualifier getQualifier() {
		return qualifier;
	}

	public Object getHolder() {
		return holder;
	}

	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		StringJoiner string = new StringJoiner(", ");

		string.add("Qualifier: " + qualifier);
		string.add("Holder: " + holder);
		string.add("Arguments: " + Arrays.toString(arguments));

		return string.toString();
	}

}
