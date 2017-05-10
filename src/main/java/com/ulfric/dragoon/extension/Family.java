package com.ulfric.dragoon.extension;

public interface Family<T extends Family<T>> {

	T getParent();

	T createChild();

}