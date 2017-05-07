package com.ulfric.dragoon.inheritance;

public interface Family<T extends Family<T>> {

	T getParent();

	T createChild();

}